package cs.b2b.mapping.scripts


import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat

import cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Booking_CSSTD
import cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Request
import cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Location
import cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.Intermodal
import cs.b2b.core.mapping.util.XmlBeanParser
import cs.b2b.core.common.xmlvalidation.ValidateXML

class CUS_CSBRXML_CS2BRXML_DUMMY {
	
	private static final String COMPLETE = 'C'
	
	private static final String OBSOLETE = 'O'

	private static final String ERROR = 'E'

	private static final String YES = 'YES'

	private static final String NO = 'NO'

	private static final String TYPE = 'Type'

	private static final String ERROR_SUPPORT = 'ES'

	private static final String IS_ERROR = 'IsError'

	private static final String VALUE = 'Value'

	private static final String ERROR_COMPLETE = 'EC'

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	cs.b2b.core.mapping.util.MappingUtil_BR_I_Common brUtil = new cs.b2b.core.mapping.util.MappingUtil_BR_I_Common(util)

	def appSessionId = null
	def sourceFileName = null
	def TP_ID = null
	def MSG_TYPE_ID = null
	def DIR_ID = null
	def MSG_REQ_ID = null
	Connection conn = null

	def xmlDateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss"
	def yyyyMMdd = "yyyyMMdd"
	def HHmm = 'HHmm'
	def yyyyMMddHHmmss = 'yyyyMMddHHmmss'

	def currentSystemDt = null

	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn){

		/**
		 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
		 */
		//		inputXmlBody = util.removeBOM(inputXmlBody)

		/**
		 * Part II: get app mapping runtime parameters
		 */
		this.conn = conn
		appSessionId = util.getRuntimeParameter("B2BSessionID", runtimeParams);
		sourceFileName = util.getRuntimeParameter("B2B_OriginalSourceFileName", runtimeParams);
		//pmt info
		TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams);
		MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
		DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams);
		MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams);

		/**
		 * Part III: read xml and prepare output xml
		 */
		//Parse the xmlBody to JavaBean
		XmlBeanParser parser = new XmlBeanParser()
		Booking_CSSTD br = parser.xmlParser(inputXmlBody, Booking_CSSTD.class)

		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		def bizKeyWriter = new StringWriter();
		def bizKeyXml = new MarkupBuilder(bizKeyWriter)
		bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		/**
		 * Part IV: mapping script start from here
		 */

		//create Start root node to Xml
		def namespace = ['xmlns:ns0':'http://www.cargosmart.com/bookingrequest',
			'xmlns:ns1':'http://www.cargosmart.com/common']
		
		def BookingRequest = outXml.createNode('ns0:BookingRequest',namespace)
		def bizKeyRoot = bizKeyXml.createNode('root')
		def txnErrorKeys = []
				
		
		//Begin work flow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		currentSystemDt = new Date()

		//Generate Msg Header
		generateHeader(br, outXml)
		
		//Start mapping
		//each XML/Booking/Request
		
		
		br.Request.eachWithIndex{current_Request, current_BodyIndex->
			List<Map<String,String>> errorKeyList = prepValidation(TP_ID,current_Request)
			
			
			if (errorKeyList.size()== 0) {
				//pass validateBeforeExecution
				//main mapping
				generate(br,current_Request, outXml)
			}
			
			String temp = ''
			temp = writer?.toString() +'</ns0:BookingRequest>'
			brUtil.buildBizKeyXML(bizKeyXml, temp, current_BodyIndex, MSG_REQ_ID, 'action', errorKeyList, TP_ID, br.InterchangeControlHeader?.MessageSessionId ,conn)
			txnErrorKeys.add(errorKeyList)
		}
	
		//End work flow
		outXml.nodeCompleted(null,BookingRequest)
			
		String result = '';
		result = writer?.toString();
		
		//brUtil.buildBizKeyXML(bizKeyXml, br, result, MSG_REQ_ID, 'action', txnErrorKeys, TP_ID, conn)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		
		//PospValidation
		List postErrors = postValidation(result, br , txnErrorKeys)
		// add post-validation error to error set
		br.Request.eachWithIndex { current_Body, current_BodyIndex ->
			if (postErrors[current_BodyIndex])
				txnErrorKeys[current_BodyIndex].add(postErrors[current_BodyIndex]);
		}
		
		
		writer.close();
		bizKeyWriter.close()

		// promote bizkey
		brUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter)
		
		def BizKey = new XmlSlurper().parseText(bizKeyWriter?.toString())
		
		boolean hasGoodBR = false
		BizKey.children().each { currentTransaction ->
			if (!(util.notEmpty(currentTransaction.AppErrorReport))) {
				hasGoodBR = true
			}
		}
		
		def nodeOutput = new XmlParser().parseText(result)
		
		if (hasGoodBR) {
			List removeBodies = new LinkedList()
			
			BizKey.children().eachWithIndex { currentTransaction, currentTransactionIndex ->
				def currentBody = nodeOutput.children().findAll {
						it.name().toString().contains('Request')
					}?.get(currentTransactionIndex)
				if ((util.notEmpty(currentTransaction.AppErrorReport))) {
					removeBodies.add(currentBody)
				}
				else{
					if (util.notEmpty(currentBody.Request.GeneralInformation.SCAC.text())){
						def insertSCAC = util.InsertSCAC(MSG_REQ_ID, DIR_ID, 'OSCAC', currentBody.Request.GeneralInformation.SCAC.text(), conn)
						def carrierId = util.getCarrierID(currentBody.Request.GeneralInformation.SCAC.text(), conn)
						def uuidId = currentBody.Request.TransactionInformation.'ns0:MessageID'.text()
						def interchangeCtlNum = currentTransaction.ControlNumberInfo.Interchange.text()
						def groupCtlNum = currentTransaction.ControlNumberInfo.Group.text()
						def transactionCtlNum = currentTransaction.ControlNumberInfo.Transaction.text()
						//def insertMonLog = util.SetMonEDIControlNo(TP_ID, carrierId, MSG_TYPE_ID, 'EDIFACT', interchangeCtlNum,groupCtlNum, transactionCtlNum, uuidId,MSG_REQ_ID, conn)
					}
				}
			}

			removeBodies.each { currentBody ->
				nodeOutput.remove(currentBody)
			}
			
			String cleanedOutputXml = util.cleanXml(XmlUtil.serialize(nodeOutput))

			return cleanedOutputXml
		}
		else{
			// prepare Node version of ShippingInstruction
			Node emptyMsg = nodeOutput.clone()
			List kids= new LinkedList()
			//remove the <Header> and all <Body>
			emptyMsg.children().eachWithIndex {kid , index->
				kids.add(kid)
			}
			kids.eachWithIndex {kid , index->
					emptyMsg.remove(kid)
			}
			
			return XmlUtil.serialize(emptyMsg)
		}

	}

	public void generateHeader(Booking_CSSTD booking, MarkupBuilder outXml){
		SimpleDateFormat sdf=new SimpleDateFormat(xmlDateTimeFormat)
		outXml.'ns0:Header'{
			'ns1:ControlNumber' booking.InterchangeControlHeader?.ControlNumber
			'ns1:MsgDT'{
				'ns1:LocDT'( 'TimeZone' : 'HKT', 'CSTimeZone' : 'HKT', sdf.format(currentSystemDt)+'+08:00')
			}
			'ns1:MsgDirection' 'I'
			'ns1:MsgType' 'BR'
			'ns1:SenderID' TP_ID
			'ns1:ReceiverID' 'CARGOSMART'
			'ns1:Action' 'NEW'
			'ns1:Version' '4.5'
			'ns1:InterchangeMessageID' MSG_REQ_ID
			'ns1:FileName' ''
			'ns1:DataSource' 'B2B'
		}
	}

	public String generate(Booking_CSSTD booking,Request current_Request, MarkupBuilder outXml){
		SimpleDateFormat sdf=new SimpleDateFormat(xmlDateTimeFormat)
		outXml.'ns0:Request'{

			'ns0:TransactionInformation'{
				UUID idOne = UUID.randomUUID();
				'ns1:MessageID' MSG_REQ_ID+','+idOne
				'ns1:InterchangeTransactionID' current_Request.GeneralInfo?.TransactionInfo?.BatchNumber
			}

			'ns0:EventInformation'{
				'ns1:EventCode' 'bkg_req'
				'ns1:EventDT'{
					'ns1:LocDT'( 'TimeZone' : 'HKT', 'CSTimeZone' : 'HKT', sdf.format(currentSystemDt)+'+08:00')
				}
			}

			'ns0:GeneralInformation'{
				'ns0:CSBookingRefNumber' ''
				'ns0:CarrierBookingNumber' current_Request.GeneralInfo?.BookingNumber

				def vActionType = null
				if (current_Request.GeneralInfo?.ActionType=="NEW"){
					vActionType="NEW"
				}else if (current_Request.GeneralInfo?.ActionType=="UPD"){
					vActionType="UPD"
				}else if (current_Request.GeneralInfo?.ActionType=="CAN"){
					vActionType="CANCEL"
				}
				'ns0:ActionType' vActionType
				'ns0:SCAC' current_Request.GeneralInfo?.SCAC
				if(current_Request.GeneralInfo?.Offices)
					'ns0:BookingOffice'{
						'ns0:BookingRegion' current_Request.GeneralInfo.Offices.BookingRegion
						'ns0:BookingOffice' current_Request.GeneralInfo.Offices.BookingOffice
						'ns0:BookingOfficeName' current_Request.GeneralInfo.Offices.BookingOfficeName
						'ns0:BLIssuingOffice' current_Request.GeneralInfo.Offices.BLIssuingOffice
					}
				if(current_Request.GeneralInfo?.Requested){
					'ns0:Requested'{
						'ns0:By' current_Request.GeneralInfo.Requested.By
						'ns0:RequestedDT'{
							'ns1:LocDT' ( 'TimeZone' : current_Request.GeneralInfo.Requested.Date?.attr_TimeZone,
							util.convertDateTime(current_Request.GeneralInfo.Requested.Date,yyyyMMddHHmmss,xmlDateTimeFormat)+'+08:00')
						}
					}
				}
				if(current_Request.GeneralInfo?.Amended){
					'ns0:Amended'{
						'ns0:By' current_Request.GeneralInfo?.Amended?.By
						'ns0:AmendedDT'{
							'ns1:LocDT' ( 'TimeZone' : current_Request.GeneralInfo.Amended.Date?.attr_TimeZone,
							util.convertDateTime(current_Request.GeneralInfo.Amended.Date,yyyyMMddHHmmss,xmlDateTimeFormat)+'+08:00')
						}
					}
				}

				//put all CargoNature
				Set<String> vCargoNatures = new HashSet<String>();
				for (vCargoDetails in current_Request.ShipmentDetails?.CargoInformation?.CargoDetails) {
					vCargoNatures.add(vCargoDetails.CargoNature)
				}
				//This logic are different between MD's and TIBCO's
				def vShipmentCargoType = null
				if (vCargoNatures.contains('RD')){
					vShipmentCargoType = 'RD'
				}else if (vCargoNatures.contains('AD')){
					vShipmentCargoType = 'AD'
				}else if (vCargoNatures.contains('DG')){
					if(vCargoNatures.contains('RF')){
						vShipmentCargoType = 'RD'
					}else if(vCargoNatures.contains('AW')){
						vShipmentCargoType = 'AD'
					}else{
						vShipmentCargoType = 'DG'
					}
				}else if (vCargoNatures.contains('RF')){
					vShipmentCargoType = 'RF'
				}else if (vCargoNatures.contains('AW')){
					vShipmentCargoType = 'AW'
				}else{
					vShipmentCargoType = 'GC'
				}
				'ns0:ShipmentCargoType' vShipmentCargoType

				def vOutBound = null
				if(current_Request.ShipmentDetails?.CargoInformation?.CargoDetails?.find{it.TrafficMode.OutBound=='FCL'}){
					vOutBound = 'FCL'
				}else if(current_Request.ShipmentDetails?.CargoInformation?.CargoDetails?.find{it.TrafficMode.OutBound=='LCL'}){
					vOutBound = 'LCL'
				}else {
					vOutBound = 'FCL'
				}
				def vInBound = null
				if(current_Request.ShipmentDetails?.CargoInformation?.CargoDetails?.find{it.TrafficMode.InBound=='FCL'}){
					vInBound = 'FCL'
				}else if(current_Request.ShipmentDetails?.CargoInformation?.CargoDetails?.find{it.TrafficMode.InBound=='LCL'}){
					vInBound = 'LCL'
				}else {
					vInBound = 'FCL'
				}
				'ns0:ShipmentTrafficMode'{
					'ns1:OutBound' vOutBound
					'ns1:InBound' vInBound
				}
				// MD's value, but TIBCO output no this field
				//'Barcode' 'BKG_OFC_BY_UNLOCDE'
				'ns0:NotificationEmailAddress' current_Request.GeneralInfo?.Requested?.NotificationEmailID
				'ns0:isNeedReplyPartyEmail' 'true'
				'ns0:CustomerBookingReference' current_Request.ShipmentDetails?.UserReferences?.References?.find{it.ReferenceType=='CR'}?.ReferenceNumber
			}

			//party  ->begin
			current_Request.ShipmentDetails.LegalParties.Party.each{current_Party->
				'ns0:Party'{
					def vPartyType = this.getPartyType(current_Party.PartyType,TP_ID, conn)
					'ns1:PartyType' vPartyType
					'ns1:PartyName' current_Party.PartyName
					// MD's value, but TIBCO output no this field
//					if(current_Party.CustomerUserID){
//						'ns1:CSCompanyID' current_Party.CustomerUserID
//					}

					def vCarrierCustomerCode = this.getCarrierCustomerCode(TP_ID,current_Request.GeneralInfo.SCAC,current_Party.CarrierCustomerCode,conn)
					if(!vCarrierCustomerCode){
						vCarrierCustomerCode = current_Party.CarrierCustomerCode
					}
					if(vCarrierCustomerCode) {
						'ns1:CarrierCustomerCode' vCarrierCustomerCode
					}

					'ns1:isNeedReplyPartyEmail' this.isNeedReplyPartyEmail(TP_ID,vPartyType,conn)


					if(current_Party?.ContactPerson){
						'ns1:Contact'{
							'ns1:FirstName' 	current_Party?.ContactPerson?.FirstName
							'ns1:LastName' 	current_Party?.ContactPerson?.LastName
							if(current_Party?.ContactPerson?.Phone){
								'ns1:ContactPhone'{
									'ns1:CountryCode' 	current_Party?.ContactPerson?.Phone?.CountryCode
									'ns1:AreaCode' 		current_Party?.ContactPerson?.Phone?.AreaCode
									'ns1:Number' 		current_Party?.ContactPerson?.Phone?.Number
								}
							}
							if(current_Party?.ContactPerson?.Fax){
								'ns1:ContactFax'{
									'ns1:CountryCode' 	current_Party?.ContactPerson?.Fax?.CountryCode
									'ns1:AreaCode' 		current_Party?.ContactPerson?.Fax?.AreaCode
									'ns1:Number' 		current_Party?.ContactPerson?.Fax?.Number
								}
								'ns1:ContactEmailAddress' 	current_Party?.ContactPerson?.Email
							}
						}
					}
					if(current_Party?.PartyLocation){
						'ns1:Address'{
							'ns1:City' 		current_Party?.PartyLocation?.City
							if(current_Party?.PartyLocation?.County){
								'ns1:County' current_Party?.PartyLocation.County
							}
							'ns1:State' 		current_Party?.PartyLocation?.StateProvince
							if(current_Party?.PartyLocation?.CountryName){
								'ns1:Country' 	current_Party?.PartyLocation?.CountryName
							}
							else{
								'ns1:Country'	current_Party?.PartyLocation?.CountryCode
							}
							'ns1:LocationCode'{
								if(current_Party?.PartyLocation?.LocationCode){
									'ns1:MutuallyDefinedCode' current_Party?.PartyLocation?.LocationCode?.MutuallyDefinedCode
									'ns1:UNLocationCode' 	 current_Party?.PartyLocation?.LocationCode?.UNLocationCode
									'ns1:SchedKDType' 		 current_Party?.PartyLocation?.LocationCode?.SchedKDCode?.attr_Type
									'ns1:SchedKDCode' 		 current_Party?.PartyLocation?.LocationCode?.SchedKDCode?.SchedKDCode
								}
							}
							'ns1:PostalCode' 	current_Party?.PartyLocation?.PostalCode
							if(current_Party?.PartyLocation?.Address){
								def Address_Concat=''
								current_Party?.PartyLocation?.Address.AddressLines.each{current_AddressLines->

									Address_Concat += (current_AddressLines+' ')
								}

								'ns1:AddressLines' {'ns1:AddressLine'  Address_Concat?.trim()}
							}
						}
					}
					if(current_Party?.SalesOfficeCode){
						'ns0:SalesOfficeCode' 	current_Party?.SalesOfficeCode
					}

					'ns0:EDIPartyType' 	current_Party.PartyType=='OTH'?'OTH':null
				}
			}
			//party  ->end

			//References  ->begin
			current_Request.ShipmentDetails?.UserReferences?.References?.each{current_Refer->
				def vType = this.getCarrierRateType(TP_ID,current_Refer?.ReferenceType,conn)
				if(vType){
					'ns0:CarrierRateReference'{
						'ns1:CSCarrierRateType' vType
						'ns1:CarrierRateNumber' util.substring(current_Refer?.ReferenceNumber,1,30)
					}
				}

			}
			current_Request.ShipmentDetails?.UserReferences?.References?.each{current_Refer->
				def vType = this.getReferenceType(TP_ID,current_Refer?.ReferenceType,conn)
				if(vType){
					'ns0:ExternalReference'{
						'ns1:CSReferenceType' vType
						'ns1:ReferenceNumber' current_Refer?.ReferenceNumber
						'ns1:ReferenceDescription' current_Refer?.ReferenceDescription
						// MD's value, but TIBCO output no this field
						//						if(vType=='OTH'){
						//							'EDIReferenceType' current_Refer?.ReferenceType
						//						}
					}
				}
			}
			//References  ->end

			current_Request.ShipmentDetails?.EquipmentInformation?.Containers.each{current_Container->
				'ns0:Container'{

					def ccsType = this.getCarrCntrSizeType(TP_ID,current_Request.GeneralInfo.SCAC,current_Container.ContainerType, conn)
					/////TODO  db Mar/06
					'ns0:CarrCntrSizeType' ccsType
					'ns0:Quantity' current_Container?.Quantity
					'ns0:GrossWeight'{
						'ns1:Weight' current_Container?.Weight.find{it.attr_Qualifier =="GROSS"}?.Weight
						'ns1:WeightUnit' current_Container?.Weight.find{it.attr_Qualifier =="GROSS"}?.attr_Units
					}
					'ns0:NetWeight'{
						'ns1:Weight' current_Container?.Weight.find{it.attr_Qualifier =="NET"}?.Weight
						'ns1:WeightUnit' current_Container?.Weight.find{it.attr_Qualifier =="NET"}?.attr_Units
					}
					'ns0:IsSOC' current_Container?.SOCIndicator=="1"? true:false
					if (current_Container?.OutBound){
						'ns0:OBDoor'{
							if(current_Container.OutBound.EmptyPickupDate){
								'ns0:EmptyPickupDT'{
									'ns1:LocDT' ( 'TimeZone' : current_Container.OutBound.EmptyPickupDate.attr_TimeZone,
									util.convertDateTime(util.substring(current_Container.OutBound.EmptyPickupDate.toString(),1,14),yyyyMMddHHmmss,xmlDateTimeFormat))
								}

								'ns0:FullReturnDT'{
									'ns1:LocDT' ( 'TimeZone' : current_Container.OutBound.FullReturnDate.attr_TimeZone,
									util.convertDateTime(util.substring(current_Container.OutBound.FullReturnDate.toString(),1,14),yyyyMMddHHmmss,xmlDateTimeFormat))
								}
							}
							current_Container.OutBound.Appointment?.each {current_Appointment->
								'ns0:DoorAppointment'{
									'ns1:AppointmentDT'{
										'ns1:LocDT' ( 'TimeZone' : current_Appointment?.AppointmentDate?.attr_TimeZone,
										util.convertDateTime(util.substring(current_Appointment?.AppointmentDate?.toString(),1,14),yyyyMMddHHmmss,xmlDateTimeFormat))

									}

									if(current_Appointment?.Location){
										'ns1:Address'{
											'ns1:City' current_Appointment.Location.City
											'ns1:County' current_Appointment.Location.County
											'ns1:State' current_Appointment.Location.StateProvince
											'ns1:Country' current_Appointment.Location.CountryName
											if(current_Appointment.Location.LocationCode){
												'ns1:LocationCode'{
													'ns1:MutuallyDefinedCode' current_Appointment.Location.LocationCode.MutuallyDefinedCode
													'ns1:UNLocationCode' current_Appointment.Location.LocationCode.UNLocationCode
													'ns1:SchedKDType' current_Appointment.Location.LocationCode.SchedKDCode?.attr_Type
													'ns1:SchedKDCode' current_Appointment.Location.LocationCode.SchedKDCode
												}
											}
											'ns1:PostalCode' current_Appointment.Location.PostalCode
											if(current_Appointment.Location.Address){
												'ns1:AddressLines'{
													def addressLine
													for(i in current_Appointment.Location.Address.AddressLines){
														if(addressLine)
															addressLine = addressLine+ " "+i
														else addressLine = i
													}
													'ns1:AddressLine' 	addressLine
												}
											}
											'ns1:Street' 	current_Appointment.Location.Street
										}
									}
									'ns1:Company' current_Appointment?.Company
									if(current_Appointment?.ContactPerson){
										'ns1:Contact'{
											'ns1:FirstName' 	current_Appointment.ContactPerson.FirstName
											'ns1:LastName' current_Appointment.ContactPerson.LastName
											if(current_Appointment?.ContactPerson.Phone){
												'ns1:ContactPhone'{
													'ns1:CountryCode' current_Appointment.ContactPerson.Phone.CountryCode
													'ns1:AreaCode' current_Appointment.ContactPerson.Phone.AreaCode
													'ns1:Number' current_Appointment.ContactPerson.Phone.Number
												}
											}
											if(current_Appointment?.ContactPerson.Fax){
												'ns1:ContactFax'{
													'ns1:CountryCode' current_Appointment.ContactPerson.Fax.CountryCode
													'ns1:AreaCode' current_Appointment.ContactPerson.Fax.AreaCode
													'ns1:Number' current_Appointment.ContactPerson.Fax.Number
												}
											}
											'ns1:ContactEmailAddress' current_Appointment.ContactPerson.Email
										}
									}
									'ns0:Quantity' current_Appointment.Quantity
								}
							}
						}
					}
					if (current_Container?.InBound){
						'ns0:IBDoor'{
							'ns0:DestinationDT'{
								'ns1:LocDT' ('TimeZone' : current_Container.InBound.DestinationDateTime?.attr_TimeZone,
								util.convertDateTime(current_Container.InBound.DestinationDateTime?.toString(),yyyyMMddHHmmss,xmlDateTimeFormat)+"+08:00"
								)
							}
						}
					}

					///少了个闭合括号 		Containers.each

					if(current_Container.OutBound||current_Container.InBound){
						'ns0:Haulage'{
							'ns1:OutBound' current_Container.OutBound.isMerchantHaulage=='1'?'M':'C'
							'ns1:InBound'  current_Container.InBound.isMerchantHaulage=='1'?'M':'C'
						}
					}

					// (katerina)
					if(current_Request.ShipmentDetails?.RouteInformation){
						def route = current_Request.ShipmentDetails?.RouteInformation
						'ns0:Route'{
							if(route.IntendedDates){
								'ns0:IntendedDT'{
									if(route.IntendedDates.From){
										'ns0:From'{
											'ns1:LocDT' ('TimeZone' : route.IntendedDates.From?.attr_TimeZone,
											util.convertDateTime(route.IntendedDates.From, "yyyyMMddHHmmss",  xmlDateTimeFormat))
										}
									}
									if(route.IntendedDates.To){
										'ns0:To'{
											'ns1:LocDT' ('TimeZone' : route.IntendedDates.To?.attr_TimeZone,
											util.convertDateTime(route.IntendedDates.To, "yyyyMMddHHmmss",  xmlDateTimeFormat))
										}
									}
									'ns0:IntendedRangeIndicator' route.IntendedDates.Range
								}
							}
							route.Location?.each{current_location->
								if(current_location.FunctionCode == 'POR'){
									'ns0:POR'{
										'ns1:LocationName' current_location.LocationName
										if(current_location.LocationDetails){
											'ns1:CityDetails'{
												'ns1:City' current_location.LocationDetails.City
												if(current_location.LocationDetails.County){
													'ns1:County' current_location.LocationDetails.County
												}
												if(current_location.LocationDetails.StateProvince){
													'ns1:State' current_location.LocationDetails.StateProvince
												}
												'ns1:Country' current_location.LocationDetails.CountryName
												if(current_location.LocationDetails.LocationCode){
													'ns1:LocationCode'{
														'ns1:MutuallyDefinedCode' current_location.LocationDetails.LocationCode.MutuallyDefinedCode
														'ns1:UNLocationCode' current_location.LocationDetails.LocationCode.UNLocationCode
														'ns1:SchedKDType' current_location.LocationDetails.LocationCode.SchedKDCode?.attr_Type
														'ns1:SchedKDCode' current_location.LocationDetails.LocationCode.SchedKDCode
													}
												}
											}
										}
									}
								}
								if(current_location.FunctionCode == 'FND'){
									'ns0:FND'{
										'ns1:LocationName' current_location.LocationName
										'ns1:CityDetails'{
											'ns1:City' current_location.LocationDetails.City
											if(current_location.LocationDetails.County){
												'ns1:County' current_location.LocationDetails.County
											}
											if(current_location.LocationDetails.StateProvince){
												'ns1:State' current_location.LocationDetails.StateProvince
											}
											'ns1:Country' current_location.LocationDetails.CountryName
											'ns1:LocationCode'{
												'ns1:MutuallyDefinedCode' current_location.LocationDetails.LocationCode.MutuallyDefinedCode
												'ns1:UNLocationCode' current_location.LocationDetails.LocationCode.UNLocationCode
												'ns1:SchedKDType' current_location.LocationDetails.LocationCode.SchedKDCode?.attr_Type
												'ns1:SchedKDCode' current_location.LocationDetails.LocationCode.SchedKDCode
											}
										}


									}
								}

							}



							Location fLocation = null
							Intermodal fIntermodal = null
							for(current_Intermodal in route.Intermodal){
								fLocation = current_Intermodal.Location.find{it.FunctionCode=='POL'}
								if(fLocation){
									fIntermodal = current_Intermodal
									break
								}
							}
							if(fLocation){
								'ns0:FirstPOL'{
									'ns1:Port'{
										'ns1:PortName' fLocation.LocationName
										'ns1:City' fLocation.LocationDetails?.City
										if(fLocation.LocationDetails?.County){
											'ns1:County' fLocation.LocationDetails?.County
										}
										if(fLocation.LocationDetails?.StateProvince){
											'ns1:State' fLocation.LocationDetails?.StateProvince
										}
										if(fLocation.LocationDetails?.LocationCode){
											'ns1:LocationCode'{
												'ns1:MutuallyDefinedCode' fLocation.LocationDetails.LocationCode.MutuallyDefinedCode
												'ns1:UNLocationCode'  fLocation.LocationDetails.LocationCode.UNLocationCode
												'ns1:SchedKDType' fLocation.LocationDetails.LocationCode.SchedKDCode?.attr_Type
												'ns1:SchedKDCode' fLocation.LocationDetails.LocationCode.SchedKDCode
											}
										}

										'ns1:Country' fLocation.LocationDetails?.CountryName
									}
									'ns0:LoadingPortVoyage' util.substring(fIntermodal.VesselVoyageInformation?.VoyageNumberDirection, 1, 17)

									if(fIntermodal.VesselVoyageInformation?.VesselInformation?.VesselName){
										'ns0:LoadingPortVesselName' fIntermodal.VesselVoyageInformation?.VesselInformation?.VesselName
									}
								}
							}
							Location lLocation = null
							for(current_Intermodal in route.Intermodal){
								fLocation = current_Intermodal.Location.find{it.FunctionCode=='POD'}
								if(fLocation)
									lLocation = fLocation
							}
							if(lLocation){
								'ns0:LastPOD'{
									'ns1:Port'{
										'ns1:PortName' lLocation.LocationName
										'ns1:City' lLocation.LocationDetails?.City
										if(lLocation.LocationDetails?.County){
											'ns1:County' lLocation.LocationDetails?.County
										}
										if(lLocation.LocationDetails?.StateProvince){
											'ns1:State' lLocation.LocationDetails?.StateProvince
										}
										if(lLocation.LocationDetails?.LocationCode){
											'ns1:LocationCode'{
												'ns1:MutuallyDefinedCode' lLocation.LocationDetails.LocationCode.MutuallyDefinedCode
												'ns1:UNLocationCode'  lLocation.LocationDetails.LocationCode.UNLocationCode
												'ns1:SchedKDType' lLocation.LocationDetails.LocationCode.SchedKDCode?.attr_Type
												'ns1:SchedKDCode' lLocation.LocationDetails.LocationCode.SchedKDCode
											}
										}

										'ns1:Country' 	lLocation.LocationDetails?.CountryName
									}

								}
							}

							if(!util.notEmpty(route.IntendedDates?.From) && route.IntendedDates?.Range == 'S'){
								'ns0:LoadingPortLatestDepDT'{
									'ns1:LocDT'  ('TimeZone' : route.IntendedDates.From?.attr_TimeZone,
										util.convertDateTime(route.IntendedDates.From,"yyyyMMddHHmmss",  xmlDateTimeFormat))

								}
							}
							Intermodal lastIntermodal = route.Intermodal[-1]
							if(lastIntermodal){
								'ns0:OceanLeg'{
									'ns0:LegSeq' route.Intermodal.size()
									Location POLLocation = lastIntermodal.Location?.find{it.FunctionCode=='POL'}
									if(POLLocation){
										'ns0:POL'{
											'ns1:Port'{
												'ns1:PortName' POLLocation.LocationName
												'ns1:City' POLLocation.LocationDetails?.City
												'ns1:County' POLLocation.LocationDetails?.County
												'ns1:State' POLLocation.LocationDetails?.StateProvince
												if(POLLocation.LocationDetails?.LocationCode){
													'ns1:LocationCode'{
														'ns1:MutuallyDefinedCode' POLLocation.LocationDetails.LocationCode.MutuallyDefinedCode
														'ns1:UNLocationCode' POLLocation.LocationDetails.LocationCode.UNLocationCode
														'ns1:SchedKDType' POLLocation.LocationDetails.LocationCode.SchedKDCode?.attr_Type
														'ns1:SchedKDCode'  POLLocation.LocationDetails.LocationCode.SchedKDCode
													}
												}

												'ns1:Country' POLLocation.LocationDetails?.CountryName
											}
										}
										Location PODLocation = lastIntermodal.Location?.find{it.FunctionCode=='POD'}
										if(PODLocation){
											'ns0:POD'{
												'ns1:Port'{
													'ns1:PortName' PODLocation.LocationName
													'ns1:City' PODLocation.LocationDetails?.City
													'ns1:County' PODLocation.LocationDetails?.County
													'ns1:State' PODLocation.LocationDetails?.StateProvince
													if(PODLocation.LocationDetails?.LocationCode){
														'ns1:LocationCode'{
															'ns1:MutuallyDefinedCode' PODLocation.LocationDetails.LocationCode.MutuallyDefinedCode
															'ns1:UNLocationCode' PODLocation.LocationDetails.LocationCode.UNLocationCode
															'ns1:SchedKDType' PODLocation.LocationDetails.LocationCode.SchedKDCode?.attr_Type
															'ns1:SchedKDCode'  PODLocation.LocationDetails.LocationCode.SchedKDCode
														}
													}

													'ns1:Country' PODLocation.LocationDetails?.CountryName
												}
											}
										}
										if(lastIntermodal.VesselVoyageInformation){
											'ns0:SVVD'{
												'ns1:Service' lastIntermodal.VesselVoyageInformation.ServiceName
												def vVessel = null
												if(lastIntermodal.VesselVoyageInformation.VesselInformation?.VesselCode){
													vVessel = lastIntermodal.VesselVoyageInformation.VesselInformation?.VesselCode
												}else{
													vVessel = lastIntermodal.VesselVoyageInformation.External?.VesselCode
												}
												if(vVessel){
													'ns1:Vessel' vVessel
												}

												if(lastIntermodal.VesselVoyageInformation.VesselInformation?.VesselName){
													'ns1:VesselName'  lastIntermodal.VesselVoyageInformation.VesselInformation?.VesselName
												}
												if(util.substring(lastIntermodal.VesselVoyageInformation?.VoyageNumberDirection,1, 17)){
													'ns1:Voyage' util.substring(lastIntermodal.VesselVoyageInformation?.VoyageNumberDirection,1, 17)
												}
												if(lastIntermodal.VesselVoyageInformation.VesselInformation?.VesselCode?.attr_LloydsCode){
													'ns1:LloydsNumber' lastIntermodal.VesselVoyageInformation.VesselInformation?.VesselCode?.attr_LloydsCode
												}
												if(lastIntermodal.VesselVoyageInformation.VesselInformation?.VesselCode?.attr_CallSign){
													'ns1:CallSign' lastIntermodal.VesselVoyageInformation.VesselInformation?.VesselCode?.attr_CallSign
												}
												if(lastIntermodal.VesselVoyageInformation.External?.VoyageNumber){
													'ns0:ExternalVoyageNumber' lastIntermodal.VesselVoyageInformation.External?.VoyageNumber
												}
											}
										}
									}
								}

							}
						}
						///katerina  ->end
					}
				}
			}
			//(sum)  Cargo 	->begin

			current_Request.ShipmentDetails?.CargoInformation.CargoDetails.each{current_Cargo->
				'ns0:Cargo'{
					'ns0:CargoInfo'{
						'ns1:CargoNature' current_Cargo?.CargoNature
						///need to replace all linefeed with space
						'ns1:CargoDescription' current_Cargo?.CargoDescription
						'ns1:Packaging'{

							//TODO
							def getPkingType = this.getPackingType(TP_ID,current_Cargo.Packaging.PackageType,conn)
							if(getPkingType){
								'ns1:PackageType' getPkingType
							}
							else{
								'ns1:PackageType'	current_Cargo.Packaging.PackageType
							}

							'ns1:PackageQty' current_Cargo?.Packaging?.Quantity
							'ns1:PackageMaterial' current_Cargo?.Packaging?.Material
						}
						'ns1:GrossWeight'{
							'ns1:Weight' current_Cargo?.Weight.find{it.attr_Qualifier=="GROSS"}?.Weight
							'ns1:WeightUnit' current_Cargo?.Weight.find{it.attr_Qualifier=="GROSS"}?.attr_Units
						}
						'ns1:NetWeight'{
							'ns1:Weight' current_Cargo?.Weight.find{it.attr_Qualifier=="NET"}?.Weight
							'ns1:WeightUnit' current_Cargo?.Weight.find{it.attr_Qualifier=="NET"}?.attr_Units
						}
						def RemarkLine
						for(i in current_Cargo?.Remarks.RemarksLines){
							if(RemarkLine)
								RemarkLine = RemarkLine+ " "+i
							else RemarkLine = i
						}

						for(j in 0..(RemarkLine?.size()-1)/70){
							'ns0:Remarks'  util.substring(RemarkLine,70*j+1,70)
						}
						if(current_Cargo?.HarmonizedTariffSchedule){
							'ns0:HarmonizedTariffSchedule' current_Cargo?.HarmonizedTariffSchedule
						}
					}
					if(current_Cargo?.ReeferSettings){
						'ns0:ReeferCargoSpec'{
							'ns1:AtmosphereType' current_Cargo.ReeferSettings.attr_AtmosphereType
							'ns1:Temperature'{
								'ns1:Temperature' current_Cargo.ReeferSettings.Temperature.Temperature
								'ns1:TemperatureUnit' current_Cargo.ReeferSettings.Temperature.attr_Units
							}
							if(current_Cargo.ReeferSettings.Ventilation){
								'ns1:Ventilation'{
									'ns1:Ventilation' current_Cargo.ReeferSettings.Ventilation.Ventilation
									'ns1:VentilationUnit' current_Cargo.ReeferSettings.Ventilation.attr_Units
								}
							}
							'ns1:GensetType' current_Cargo.ReeferSettings.attr_GenSetType
							def ReeferRemark
							for(i in current_Cargo.ReeferSettings.Remarks?.RemarksLines){
								if(ReeferRemark)
									ReeferRemark = ReeferRemark+ " "+i
								else ReeferRemark = i
							}
							'ns1:Remarks' util.substring(ReeferRemark,1,350)
							'ns1:CO2' current_Cargo.ReeferSettings.attr_CO2
							'ns1:O2' current_Cargo.ReeferSettings.attr_O2
							'ns1:VentSettingCode' current_Cargo.ReeferSettings.attr_VentSettingCode
							'ns1:DehumidityPercentage' current_Cargo.ReeferSettings.DehumidityPercentage
							'ns1:SensitiveCargoDesc' current_Cargo.ReeferSettings.SensitiveCargoDesc
							'ns1:IsPreCoolingReq' current_Cargo.ReeferSettings.attr_PreCooling=='0'?'false':'true'
							if(current_Cargo.ReeferSettings.EmergencyContact){
								'ns1:EmergencyContact'{
									'ns1:FirstName' current_Cargo.ReeferSettings.EmergencyContact.FirstName
									'ns1:LastName' current_Cargo.ReeferSettings.EmergencyContact.LastName
									if(current_Cargo.ReeferSettings.EmergencyContact.Phone){
										'ns1:ContactPhone'{
											'ns1:CountryCode' current_Cargo.ReeferSettings.EmergencyContact.Phone.CountryCode
											'ns1:AreaCode' current_Cargo.ReeferSettings.EmergencyContact.Phone.AreaCode
											'ns1:Number' current_Cargo.ReeferSettings.EmergencyContact.Phone.Number
										}
									}
									if(current_Cargo.ReeferSettings.EmergencyContact.Fax){
										'ns1:ContactFax'{
											'ns1:CountryCode' current_Cargo.ReeferSettings.EmergencyContact.Fax.CountryCode
											'ns1:AreaCode' current_Cargo.ReeferSettings.EmergencyContact.Fax.AreaCode
											'ns1:Number' current_Cargo.ReeferSettings.EmergencyContact.Fax.Number
										}
									}
									'ns1:ContactEmailAddress' current_Cargo.ReeferSettings.EmergencyContact.Email
									'ns1:Type' current_Cargo.ReeferSettings.EmergencyContact.Type.toUpperCase()
								}
							}
						}
					}
					current_Cargo?.DangerousCargo?.DangerousCargoInfo?.each{current_DngInfo->
						'ns0:DGCargoSpec'{
							'ns1:IMDGPage' current_DngInfo?.HazardousMaterial?.IMCOPage
							'ns1:IMOClass' current_DngInfo?.HazardousMaterial?.IMCOClass
							'ns1:UNNumber' current_DngInfo?.HazardousMaterial?.UNNumber
							'ns1:TechnicalName' current_DngInfo?.HazardousMaterial?.TechnicalShippingName
							'ns1:ProperShippingName' current_DngInfo?.HazardousMaterial?.ProperShippingName
							if(current_DngInfo?.HazardousMaterial?.Package){
								'ns1:PackageGroup'{
									'ns1:Code' current_DngInfo.HazardousMaterial.Package.PackagingGroupCode
									if(current_DngInfo.HazardousMaterial.Package.InnerPackaging){
										def iType = this.getPackageType(current_DngInfo.HazardousMaterial.Package.InnerPackaging.attr_Units,conn)
										'ns1:InnerPackageDescription'{
											'ns1:PackageType' iType?iType:util.substring(current_DngInfo.HazardousMaterial.Package.InnerPackaging.attr_Units,1,3)
											'ns1:PackageQty' current_DngInfo.HazardousMaterial.Package.InnerPackaging.toString()
										}
									}
									if(current_DngInfo.HazardousMaterial.Package.OuterPackaging){
										def oType = this.getPackageType(current_DngInfo.HazardousMaterial.Package.OuterPackaging.attr_Units,conn)

										'ns1:OuterPackageDescription'{
											'ns1:PackageType' oType?oType:util.substring(current_DngInfo.HazardousMaterial.Package.OuterPackaging.attr_Units,1,3)
											'ns1:PackageQty' current_DngInfo.HazardousMaterial.Package.OuterPackaging.toString()
										}
									}
								}
							}
							'ns1:MFAGNumber' current_DngInfo?.HazardousMaterial?.MFAGTableNumber
							'ns1:EMSNumber' current_DngInfo?.HazardousMaterial?.EMSNumber

							'ns1:GrossWeight'{
								'ns1:Weight' current_DngInfo?.HazardousMaterial?.Weight.find{it.attr_Qualifier=="GROSS"}?.Weight
								'ns1:WeightUnit' current_DngInfo?.HazardousMaterial?.Weight.find{it.attr_Qualifier=="GROSS"}?.attr_Units
							}
							'ns1:NetWeight'{
								'ns1:Weight' current_DngInfo?.HazardousMaterial?.Weight.find{it.attr_Qualifier=="NET"}?.Weight
								'ns1:WeightUnit' current_DngInfo?.HazardousMaterial?.Weight.find{it.attr_Qualifier=="NET"}?.attr_Units
							}
							if(current_DngInfo?.HazardousMaterial?.FlashPoint){
								'ns1:FlashPoint'{
									'ns1:Temperature' current_DngInfo.HazardousMaterial.FlashPoint.toString()
									'ns1:TemperatureUnit' current_DngInfo.HazardousMaterial.FlashPoint.attr_Units
								}
							}
							if(current_DngInfo?.HazardousMaterial?.DGElevationTemperature){
								'ns1:ElevatedTemperature'{
									'ns1:Temperature' current_DngInfo.HazardousMaterial.DGElevationTemperature.toString()
									'ns1:TemperatureUnit' current_DngInfo.HazardousMaterial.DGElevationTemperature.attr_Units
								}
							}
							'ns1:isLimitedQuantity' current_DngInfo?.HazardousMaterial?.Indicators?.IsLimitedQuantity=='0'?'false':'true'
							'ns1:IsInhalationHazardous' current_DngInfo?.HazardousMaterial?.Indicators?.IsInhalationHazardous=='0'?'false':'true'
							'ns1:IsReportableQuantity' current_DngInfo?.HazardousMaterial?.Indicators?.IsReportableQuantity=='0'?'false':'true'
							'ns1:IsEmptyUnclean' current_DngInfo?.HazardousMaterial?.Indicators?.IsEmptyUnclean=='0'?'false':'true'
							'ns1:isMarinePollutant' current_DngInfo?.HazardousMaterial?.Indicators?.IsLimitedQuantity=='0'?'false':'true'
							def HazaRemark
							for(i in current_DngInfo?.HazardousMaterial?.Remarks?.RemarksLines){
								if(HazaRemark)
									HazaRemark = HazaRemark+ " "+i
								else HazaRemark = i
							}
							'ns1:Remarks' util.substring(HazaRemark,1,240)
							current_DngInfo?.EmergencyContact?.each{current_Contact->
								'ns1:EmergencyContact'{
									'ns1:FirstName' current_Contact?.FirstName
									'ns1:LastName' current_Contact?.LastName
									if(current_Contact?.Phone){
										'ns1:ContactPhone'{
											'ns1:CountryCode' current_Contact.Phone.CountryCode
											'ns1:AreaCode' current_Contact.Phone.AreaCode
											'ns1:Number' current_Contact.Phone.Number
										}
									}
									if(current_Contact?.Fax){
										'ns1:ContactFax'{
											'ns1:CountryCode' current_Contact.Fax.CountryCode
											'ns1:AreaCode' current_Contact.Fax.AreaCode
											'ns1:Number' current_Contact.Fax.Number
										}
									}
									'ns1:ContactEmailAddress' current_Contact.Email
									'ns1:Type' current_Contact.Type.toUpperCase()
								}
							}
							'ns0:Description' current_DngInfo?.HazardousMaterial?.MaterialDescription
						}
					}
					current_Cargo?.AwkwardCargo?.AwkwardCargoInfo?.each{ current_AwkInfo->
						'ns0:AWCargoSpec'{
							'ns1:Height'{
								'ns1:Length' current_AwkInfo?.AwkwardCargoDetails?.Height?.toString()
								'ns1:LengthUnit' current_AwkInfo?.AwkwardCargoDetails?.Height?.attr_Units
							}
							'ns1:Length'{
								'ns1:Length' current_AwkInfo?.AwkwardCargoDetails?.Length?.toString()
								'ns1:LengthUnit' current_AwkInfo?.AwkwardCargoDetails?.Length?.attr_Units
							}
							'ns1:Width'{
								'ns1:Length' current_AwkInfo?.AwkwardCargoDetails?.Width?.toString()
								'ns1:LengthUnit' current_AwkInfo?.AwkwardCargoDetails?.Width?.attr_Units
							}
							def AwkInfoRemark
							for(i in current_AwkInfo?.AwkwardCargoDetails?.Remarks?.RemarksLines){
								if(AwkInfoRemark)
									AwkInfoRemark = AwkInfoRemark+ " "+i
								else AwkInfoRemark = i
							}
							'ns1:Remarks' util.substring(AwkInfoRemark,1,240)
						}
					}
					'ns0:TrafficMode'{
						'ns1:OutBound' current_Cargo?.TrafficMode?.OutBound=='LCL'?'LCL':'FCL'
						'ns1:InBound' current_Cargo?.TrafficMode?.InBound=='LCL'?'LCL':'FCL'
					}
				}
			}
			'ns0:SpecialInstruction'{
				current_Request.SummaryDetails?.SpecialHandling?.each{current_SpecialHandling->
					'ns0:SpecialHandling'{
						'ns1:Code' current_SpecialHandling.Code
						'ns1:Description' current_SpecialHandling.SpecialInstructions
					}
					// MD's value, but TIBCO output no this field
					//					'ExportDeclarationRequired' 'NS'
				}
			}
			def SumRemark
			for(i in current_Request?.SummaryDetails?.Remarks?.find{it.Type=="01"}?.Content?.RemarksLines){
				if(SumRemark)
					SumRemark = SumRemark+ " "+i
				else SumRemark = i
			}
			'ns0:Remarks' ('RemarkType' : '01',util.substring(SumRemark,1,240))
		}
	}


	private String getPackingType(String TP_ID, String EXT_CDE, Connection conn) {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select INT_CDE from b2b_cde_conversion where TP_ID=? and msg_type_id='BR' and DIR_ID='I' and convert_type_id='PackageUnit' and EXT_CDE=?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10000);
			pre.setQueryTimeout(10);
			pre.setString(1, TP_ID);
			pre.setString(2, EXT_CDE);
			result = pre.executeQuery();

			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	private String getCarrCntrSizeType(String TP_ID, String scac_cde, String EXT_CDE, Connection conn) {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select INT_CDE from b2b_cde_conversion where TP_ID=? and DIR_ID='I' and convert_type_id='ContainerType' and msg_type_id='BR' and msg_fmt_id='XML' and scac_cde=? and EXT_CDE=?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10000);
			pre.setQueryTimeout(10);
			pre.setString(1, TP_ID);
			pre.setString(2, scac_cde);
			pre.setString(3, EXT_CDE);
			result = pre.executeQuery();

			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	private String getPackageType(String ext_cde, Connection conn) {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "SELECT int_cde FROM b2b_cde_conversion WHERE convert_type_id = 'PackageUnit' AND dir_id = 'I' AND msg_type_id = 'BR' AND tp_id is null  AND msg_fmt_id = 'BRXML' AND scac_cde IS NULL AND ext_cde = ?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10000);
			pre.setQueryTimeout(10);
			pre.setString(1, ext_cde);
			//pre.setString(1, convert_type_id);
			//pre.setString(2, INT_CDE);
			//pre.setString(3, msg_type_id);
			//pre.setString(4, msg_fmt_id);
			//pre.setString(5, tp_id);
			result = pre.executeQuery();

			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}


	private String getCarrierRateType(String tp_id, String ext_cde, Connection conn) {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "SELECT int_cde FROM b2b_cde_conversion WHERE convert_type_id = 'CarrierRateType' AND dir_id = 'I' AND msg_type_id = 'BR' AND tp_id = ?  AND msg_fmt_id = 'XML' AND scac_cde IS NULL AND ext_cde = ?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10000);
			pre.setQueryTimeout(10);
			pre.setString(1, tp_id);
			pre.setString(2, ext_cde);
			result = pre.executeQuery();

			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	private String getReferenceType(String tp_id, String ext_cde, Connection conn) {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "SELECT int_cde FROM b2b_cde_conversion WHERE convert_type_id = 'ReferenceType' AND dir_id = 'I' AND msg_type_id = 'BR' AND tp_id = ?  AND msg_fmt_id = 'XML' AND scac_cde IS NULL AND ext_cde = ?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10000);
			pre.setQueryTimeout(10);
			pre.setString(1, tp_id);
			pre.setString(2, ext_cde);
			result = pre.executeQuery();

			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	private String getPartyType(String EXT_CDE, String tp_id, Connection conn) {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select INT_CDE from B2B_CDE_CONVERSION where convert_type_id='LegalPartyType' and EXT_CDE=? and msg_type_id='BR' and msg_fmt_id='XML' and tp_id=?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10000);
			pre.setQueryTimeout(10);
			pre.setString(1, EXT_CDE);
			pre.setString(2, tp_id);
			result = pre.executeQuery();

			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	private String getCarrierCustomerCode(String TP_ID, String SCAC_CDE, String EXT_CDE, Connection conn) {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select INT_CDE from b2b_cde_conversion where TP_ID=? and convert_type_id='CarrierCustCode' and msg_type_id='BR' and msg_fmt_id='XML' and SCAC_CDE= ? and EXT_CDE=? and  ACTIVE_FLAG='T'";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10000);
			pre.setQueryTimeout(10);
			pre.setString(1, TP_ID);
			pre.setString(2, SCAC_CDE);
			pre.setString(3, EXT_CDE);
			result = pre.executeQuery();

			if (result.next()) {
				ret = result.getString(1);
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	private boolean isNeedReplyPartyEmail(String tp_id, String Ext_cde, Connection conn) {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "Select * from b2b_edi_cde_ref where tp_id=? and convert_type_id='ReplyPartyEmail' and Seg_id='ReplyPartyEmail' and seg_num='ReplyPartyEmail' and Ext_cde = ?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10000);
			pre.setQueryTimeout(10);
			pre.setString(1, tp_id);
			pre.setString(2, Ext_cde);
			result = pre.executeQuery();

			if (result.next()) {
				return true;
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return false;
	}

	private List<Map<String,String>> prepValidation(String tp_id, def current_Body) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()
		
		//check SCAC
		if (brUtil.checkSCAC(current_Body.GeneralInfo?.SCAC, conn)=="") {
			Map<String,String> errorKey = null
			errorKey = [TYPE: ERROR , IS_ERROR: YES,  VALUE: 'Invalid SCAC on the input']
			errorKeyList.add(errorKey)
		}
		/*
		//Check tp_integration_asso
		if (brUtil.checkTpIntegrationAsso(tp_id, 'BR', current_Body.GeneralInfo?.SCAC, conn)) {
			Map<String,String> errorKey = null
			errorKey = [TYPE: ERROR , IS_ERROR: YES,  VALUE: 'SCAC : ' + current_Body.GeneralInfo?.SCAC +' not integate with customer']
			errorKeyList.add(errorKey)
		}
		*/
		return errorKeyList;
	}

	public List postValidation(def output, def input, def txnErrorKeys) {
		List AppErrors = new ArrayList()
		
		def nodeBookingRequest = new XmlParser().parseText(output)
		
		// prepare schema validator
		cs.b2b.core.common.xmlvalidation.ValidateXML validator = new ValidateXML()
		// prepare Node version of ShippingInstruction
		Node msgWithHeader = nodeBookingRequest.clone()
		List kids= new LinkedList()
		//remove the <Body>
		msgWithHeader.children().eachWithIndex {kid , index->
			if (index !=0){
				kids.add(kid)
			}
		}
		kids.eachWithIndex {kid , index->
			msgWithHeader.remove(kid)
		}
		
		input.Request.eachWithIndex { current_Body, current_BodyIndex ->
			if (!(txnErrorKeys[current_BodyIndex])){
				// do Post-validation on Body (
				Node currentBody = nodeBookingRequest.children().get(current_BodyIndex+1)
				Node currentMsg = msgWithHeader.clone()
				// prepare a complete transaction with one header & one body
				currentMsg.append(currentBody)
				String validationResult = validator.xmlValidation('CS2-CUS-BRXML', XmlUtil.serialize(currentMsg))
				if (validationResult.contains('Validation Failure.')){
					AppErrors.add(validationResult)
				}
			}
			// for empty Body i.e. pre-validation fails
			else AppErrors.add("")
		}
		return AppErrors
	}
}
