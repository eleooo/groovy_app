import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();

	/**
	 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
	 */
	//inputXmlBody = util.removeBOM(inputXmlBody)

	/**
	 * Part II: get OLL mapping runtime parameters
	 */
	//[]{"B2BSessionID="+B2BSessionID, "B2B_OriginalSourceFileName="+B2B_OriginalSourceFileName, "B2B_SendPortID="+B2B_SendPortID, "PortProperty="+PortProperty, "MSG_REQ_ID="+MSG_REQ_ID, "TP_ID="+TP_ID, "MSG_TYPE_ID="+MSG_TYPE_ID, "DIR_ID="+DIR_ID};
	def appSessionId = util.getRuntimeParameter("B2BSessionID", runtimeParams);
	def sourceFileName = util.getRuntimeParameter("B2B_OriginalSourceFileName", runtimeParams);
	//pmt info
	def TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams);
	def MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
	def DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams);
	def MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams);
	
	/**
	 * Part III: read xml and prepare output xml
	 */
	//Important: the inputXml is xml root element
	def parser = new XmlParser()
	parser.setNamespaceAware(false);
	def ContainerMovement = parser.parseText(inputXmlBody);

	def writer = new StringWriter()
	def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print

	def bizKeyWriter = new StringWriter();
	def bizKeyXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(bizKeyWriter), "", false)); 
	bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
	def bizKeyGroup = bizKeyXml.createNode('group')
	
	def csuploadWriter = new StringWriter();
	def csuploadXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(csuploadWriter), "", false));
	csuploadXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
	def csuploadGroup = csuploadXml.createNode('group')
	
	TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
	/**
	 * Part IV: mapping script start from here
	 */
	
	//BeginWorkFlow
	prepare(appSessionId,ContainerMovement)
	
	def prepValidationResult = validateBeforeExecution(csuploadXml,bizKeyXml,ContainerMovement,util,conn,TP_ID, MSG_TYPE_ID,MSG_REQ_ID)
	println prepValidationResult
	if(prepValidationResult.findAll{it.size == 0}.size != 0){
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
		execute(outXml, ContainerMovement, util, conn, TP_ID, MSG_TYPE_ID)
	}
	
	validateAfterExecution()
	
	beforeEnd()
	
	//EndWorkFlow
	
	bizKeyXml.nodeCompleted(null,bizKeyGroup)
	csuploadXml.nodeCompleted(null,csuploadGroup)
	
	println bizKeyWriter.toString();
	println csuploadWriter.toString();

	cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId,'PROMOTE_SESSION_BIZKEY',bizKeyWriter.toString());
	cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId,'PROMOTE_SESSION_CSUPLOAD',csuploadWriter.toString());

	return writer?.toString();
}

private void prepare(def appSessionId, Node ContainerMovement){
	cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId,'PROMOTE_InterchangeMessageID',ContainerMovement.Header.InterchangeMessageID.text());
	cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId,'PROMOTE_CarrierSCAC',ContainerMovement.Body[0]?.GeneralInformation?.SCAC?.text());
}

private def validateBeforeExecution(MarkupBuilder csuploadXml,MarkupBuilder bizKeyXml,Node ContainerMovement,cs.b2b.core.mapping.util.MappingUtil util,Connection conn,def TP_ID, def MSG_TYPE_ID, def MSG_REQ_ID){
	List<Map<String,String>> csuploadErrorKey = null;
	def txnErrorKeys = [];
	def errorKey = null
	
	def interchange = util.convertDateTime(ContainerMovement.Header.MsgDT.LocDT.text(),"yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
	def scacTpId = getSCACTpId(ContainerMovement.Body.GeneralInformation.SCAC.text(), conn) 

	ContainerMovement.Body.eachWithIndex  { current_Body, current_BodyIndex ->
		
		csuploadErrorKey = new ArrayList<Map<String,String>>();
		
		def eventCS2Cde = current_Body.Event.CS1Event.text()
		def eventExtCde = util.getConversion('DSGOODS', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)
		
		if(!util.notEmpty(eventExtCde)){
			errorKey = ['Type':'ES','Value':eventCS2Cde + ' - Missing Status Code','IsError':'YES']
			csuploadErrorKey.add(errorKey);
		}
		if(!util.notEmpty(current_Body.Event.EventDT.LocDT.text())){
			errorKey = ['Type':'ES','Value':eventCS2Cde + ' - Missing Status Event Date','IsError':'YES']
			csuploadErrorKey.add(errorKey);
		}
		if(current_Body.Container.findAll{util.notEmpty(it?.ContainerNumber?.text())}.size == 0){
			errorKey = ['Type':'EC','Value':eventCS2Cde + ' - Missing Container Number','IsError':'YES']
			csuploadErrorKey.add(errorKey);
		}
		if(current_Body.Event.CS1Event.text() == 'CS130' || current_Body.Event.CS1Event.text() == 'CS140'){
			errorKey = ['Type':'C','Value':eventCS2Cde + ' - Event not subscribed by Customer','IsError':'NO']
			csuploadErrorKey.add(errorKey);
		}
		if(current_Body.BLGeneralInfo.findAll{util.notEmpty(it?.BLNumber?.text())}.size == 0){
			errorKey = ['Type':'EC','Value':eventCS2Cde + ' - Missing BL Number','IsError':'NO']
			csuploadErrorKey.add(errorKey);
		}
		if(current_Body.Party.findAll{it.PartyType.text() == 'CGN' && util.notEmpty(it.CarrierCustomerCode.text())}.size == 0){
			errorKey = ['Type':'C','Value':eventCS2Cde + ' - Missing Consignee Code','IsError':'NO']
			csuploadErrorKey.add(errorKey);
		}
		
		txnErrorKeys.add(csuploadErrorKey)
		
		buildCsupload(csuploadXml, csuploadErrorKey,String.format('%19s',current_Body.TransactionInformation.InterchangeTransactionID.text())?.replace(" ","0"),MSG_REQ_ID)
		buildBizKey(bizKeyXml, current_BodyIndex, current_Body, util,conn, csuploadErrorKey,interchange,scacTpId,TP_ID,eventExtCde)
	}

	
	return txnErrorKeys;
}

private void execute(MarkupBuilder outXml, Node ContainerMovement, cs.b2b.core.mapping.util.MappingUtil util, Connection conn, TP_ID, MSG_TYPE_ID) {
	
	def currentSystemDt = new Date()
	
	def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, "X.12", conn)
	long ediIsaCtrlNumber = ctrlNos[0]
	long ediGroupCtrlNum = ctrlNos[1]
	
	outXml.'T315' {
		'ISA' {
			'I01_01' '00'
			'I02_02' '          '
			'I03_03' '00'
			'I04_04' '          '
			'I05_05' 'ZZ'
			'I06_06' 'CARGOSMART     '
			'I05_07' 'ZZ'
			'I07_08' 'APLUNET        '
			'I08_09' currentSystemDt.format("yyMMdd")
			'I09_10' currentSystemDt.format("HHmm")
			'I10_11' 'U'
			'I11_12' '00401'
			'I12_13' String.format("%09d", ediIsaCtrlNumber)
			'I13_14' '0'
			'I14_15' 'P'
			'I15_16' '>'
		}
		'GS' {
			'E479_01' 'QO'
			'E142_02' 'CARGOSMART'
			'E124_03' 'APLUNET'
			'E373_04' currentSystemDt.format("yyyyMMdd")
			'E337_05' currentSystemDt.format("HHmm")
			'E28_06' ediGroupCtrlNum
			'E455_07' 'X'
			'E480_08' '004010'
		}
		ContainerMovement.Body.eachWithIndex { current_Body, current_BodyIndex ->
			def currentSTCtrlNum = ediGroupCtrlNum.toString() + String.format("%04d", current_BodyIndex+1)
			'Loop_ST' {
				'ST' {
					'E143_01' '315'
					'E329_02' currentSTCtrlNum
				}
				'B4' {
					'E157_03' util.getConversion('DSGOODS', 'CT', 'O', 'EventStatusCode', current_Body.Event.CS1Event.text(), conn)
					
					'E373_04' util.convertDateTime(current_Body.Event.EventDT.LocDT.text(), "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
					
					'E161_05' util.convertDateTime(current_Body.Event.EventDT.LocDT.text(), "yyyy-MM-dd'T'hh:mm:ss", 'HHmm')
					
					'E159_06' String.format('%-5s', current_Body.Event.Location.LocationCode.UNLocationCode.text())
					
					'E206_07' current_Body.Container?.ContainerNumber?.text().substring(0,4)
					
					'E207_08' current_Body.Container?.ContainerNumber?.text().substring(4) + current_Body.Container.ContainerCheckDigit.text()
					
					if (current_Body.Container?.ContainerStatus?.text().trim() == 'Empty') {
						'E578_09' 'E'
					} else if (current_Body.Container?.ContainerStatus?.text().trim() =='Laden') {
						'E578_09' 'L'
					} else if (current_Body.Event?.CS1Event.text().trim() =='CS210') {
						'E578_09' 'E'
					} else {
						'E578_09' 'L'
					}
					'E24_10' current_Body.Container?.CarrCntrSizeType?.text().substring(0,4)
					if (current_Body.Event.Location?.CityDetails?.City?.text().trim() != '') {
						'E310_11' current_Body.Event.Location.CityDetails.City.text().trim().toUpperCase()
					} else if (current_Body.Event?.Location?.LocationName?.text().trim() != '') {
						'E310_11' current_Body.Event.Location.LocationName.text().trim().toUpperCase()
					} else if (current_Body.Event?.Location?.LocationCode?.UNLocationCode?.text().trim() != '') {
						'E310_11' current_Body.Event.Location.LocationCode.UNLocationCode.text().trim().toUpperCase()
					}
					'E309_12' 'UN'
				}
				//Loop N9
				current_Body.BLGeneralInfo.each { current_BLGeneralInfo ->
					if (current_BLGeneralInfo.BLNumber?.text().trim() !='') {
						'N9' {
							'E128_01' 'BM'
							'E127_02' current_BLGeneralInfo.BLNumber.text().trim()
						}
					}
					if (current_BLGeneralInfo.CustomsReferenceType?.text().trim() == 'IT' && current_BLGeneralInfo.CustomsReferenceNumber?.text().trim()!='') {
						'N9' {
							'E128_01' 'IB'
							'E127_02' current_BLGeneralInfo.CustomsReferenceNumber.text().trim()
						}
					}
				}
				current_Body.BookingGeneralInfo.each { current_BookingGeneralInfo ->
					if(current_BookingGeneralInfo.CarrierBookingNumber?.text().trim() != ''){
						'N9' {
							'E128_01' 'BN'
							'E127_02' current_BookingGeneralInfo.CarrierBookingNumber.text().trim()
						}
					}
				}
				if(current_Body.Container?.ContainerNumber?.text() != '' && current_Body.Container?.ContainerCheckDigit.text() != '') {
					'N9' {
						'E128_01' 'EQ'
						'E127_02' current_Body.Container.ContainerNumber.text() + current_Body.Container?.ContainerCheckDigit.text()
					}
				}
				def partyCgnCCC = current_Body.Party?.find{it.PartyType.text() == 'CGN'}?.CarrierCustomerCode
				if (partyCgnCCC) {
					'N9' {
						'E128_01' 'IC'
						'E127_02' partyCgnCCC.text().trim()
						'E369_03' current_Body.Party.find{it.PartyType.text() == 'CGN'}?.PartyName?.text()?.trim()
					}
				}
				current_Body.Container.Seal.each { current_Seal ->
					if( current_Seal.SealNumber.text() != '' && current_Seal.SealNumber?.text().trim().length() >= 4){
						'N9' {
							'E128_01' 'SN'
							'E127_02' current_Seal.SealNumber?.text().trim()
						}
					}
				}
				
				def shipDir =  util.getConversionWithoutTP('CT','O','EventDirection', current_Body.Event.CS1Event.text(), conn)
				def firstOceanLeg =  current_Body.Route.OceanLeg[0]
				def lastOceanLeg =  current_Body.Route.OceanLeg[-1]
				if( shipDir=='I' || (shipDir=='OB' && current_Body.Route.FND?.CSStandardCity?.CSContinentCode.text() != '' &&
					current_Body.Route.FND.CSStandardCity.CSCountryCode.text() == current_Body.Event.Location.CSStandardCity.CSCountryCode.text())) {
					
					'Q2' {
						if(lastOceanLeg.SVVD.Discharge.LloydsNumber) {
							'E597_01' util.substring(lastOceanLeg.SVVD.Discharge.LloydsNumber.text(), 1, 7)
						}
						if(lastOceanLeg.SVVD.Discharge.RegistrationCountryCode) {
							'E26_02' lastOceanLeg.SVVD.Discharge.RegistrationCountryCode.text()
						}
						def polDepartDtA = lastOceanLeg.POL.DepartureDT.find{it.@Indicator =='A'}?.LocDT
						def polDepartDtE = lastOceanLeg.POL.DepartureDT.find{it.@Indicator =='E'}?.LocDT
						if (polDepartDtA) {
							'E373_04' util.convertDateTime(polDepartDtA.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
						} else if(polDepartDtE) {
							'E373_04' util.convertDateTime(polDepartDtE.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
						}
						def podArrivalDtA = lastOceanLeg.POD.ArrivalDT.find{it.@Indicator=='A'}?.LocDT
						def podArrivalDtE = lastOceanLeg.POD.ArrivalDT.find{it.@Indicator=='E'}?.LocDT
						if (podArrivalDtA) {
							'E373_05' util.convertDateTime(podArrivalDtA.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
						} else if (podArrivalDtE) {
							'E373_05' util.convertDateTime(podArrivalDtE.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
						}
						if (lastOceanLeg.SVVD.Discharge.Voyage || lastOceanLeg.SVVD.Discharge.Direction) {
							'E55_09' lastOceanLeg.SVVD.Discharge.Voyage?.text() + lastOceanLeg.SVVD.Discharge.Direction?.text()
						}
						'E127_11' current_Body.GeneralInformation.SCAC.text()
						'E897_12' 'L'
						if (lastOceanLeg.SVVD.Discharge.VesselName) {
							'E182_13' lastOceanLeg.SVVD.Discharge.VesselName.text()
						}
					}
				} else if (shipDir=='O' || shipDir=='OB') {
					'Q2' {
						if (firstOceanLeg.SVVD.Loading.LloydsNumber) {
							'E597_01' util.substring(firstOceanLeg.SVVD.Loading.LloydsNumber.text(), 1, 7)
						}
						if (firstOceanLeg.SVVD?.Loading.RegistrationCountryCode) {
							'E26_02' firstOceanLeg.SVVD.Loading.RegistrationCountryCode.text()
						}
						def polDepartureDtA = firstOceanLeg.POL?.DepartureDT?.find{it.@Indicator == 'A'}?.LocDT
						def polDepartureDtE = firstOceanLeg.POL?.DepartureDT?.find{it.@Indicator == 'E'}?.LocDT
						if (polDepartureDtA) {
							'E373_04' util.convertDateTime(polDepartureDtA.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
						} else if (polDepartureDtE) {
							'E373_04' util.convertDateTime(polDepartureDtE.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
						}
						
						def podArrivalDtA = firstOceanLeg.POD?.ArrivalDT?.find{it.@Indicator=='A'}?.LocDT
						def podArrivalDtE = firstOceanLeg.POD?.ArrivalDT?.find{it.@Indicator=='E'}?.LocDT
						if (podArrivalDtA) {
							'E373_05' util.convertDateTime(podArrivalDtA.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
						} else if (podArrivalDtE) {
							'E373_05' util.convertDateTime(podArrivalDtE.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
						}
						if (firstOceanLeg.SVVD.Loading.Voyage || firstOceanLeg.SVVD.Loading.Direction) {
							'E55_09' firstOceanLeg.SVVD.Loading.Voyage?.text() + firstOceanLeg.SVVD.Loading.Direction?.text()
						}
						'E127_11' current_Body.GeneralInformation.SCAC?.text()
						'E897_12' 'L'
						if (firstOceanLeg.SVVD.Loading.VesselName) {
							'E182_13' firstOceanLeg.SVVD.Loading.VesselName.text()
						}
					}
				}
				
				//R4
				def POR =  current_Body.Route.POR
				def firstPOL =  current_Body.Route.FirstPOL
				def lastPOD =  current_Body.Route.LastPOD
				def FND =  current_Body.Route.FND
				'Loop_R4' {
					'R4' {
						'E115_01' 'R'
						if (POR.CityDetails.LocationCode.UNLocationCode) {
							'E309_02' 'UN'
						} else if (POR.CityDetails.LocationCode.SchedKDType) {
							'E309_02' POR.CityDetails.LocationCode.SchedKDCode.text()
						}
						if (POR.CityDetails.LocationCode.UNLocationCode) {
							'E310_03' POR.CityDetails.LocationCode.UNLocationCode.text()
						} else if (POR.CityDetails.LocationCode.SchedKDCode) {
							'E310_03' POR.CityDetails.LocationCode.SchedKDCode.text()
						}
						if (POR.CityDetails.City) {
							'E114_04' util.substring(POR.CityDetails.City?.text().toUpperCase(), 1, 24)
						}
						if (POR.CSStandardCity.CSCountryCode.text().length() >= 2){
							'E26_05' POR.CSStandardCity.CSCountryCode.text()
						}
						if (POR.CSStandardCity.CSStateCode) {
							'E156_08' POR.CSStandardCity.CSStateCode.text()
						}
					}
				}
				
				'Loop_R4' {
					'R4' {
						'E115_01' 'L'
						if (firstPOL.Port.LocationCode.UNLocationCode) {
							'E309_02' 'UN'
						} else if (firstPOL.Port.LocationCode.SchedKDCode) {
							'E309_02' firstPOL.Port.LocationCode.SchedKDCode.text()
						}
						if (firstPOL.Port.LocationCode.UNLocationCode) {
							'E310_03' firstPOL.Port.LocationCode.UNLocationCode.text()
						} else if (firstPOL.Port.LocationCode.SchedKDCode) {
							'E310_03' firstPOL.Port.LocationCode.SchedKDCode.text()
						}
						if (firstPOL.Port.City) {
							'E114_04' util.substring(firstPOL.Port.City.text().toUpperCase(), 1, 24)
						}
						if (firstPOL.Port?.CSCountryCode?.text().length() >= 2) {
							'E26_05' firstPOL.Port.CSCountryCode.text()
						}
						if (firstPOL.CSStateCode ){
							'E156_08' firstPOL.CSStateCode.text()
						}
					}
					def polDepartureDtA = firstOceanLeg.POL.DepartureDT.find{it.@Indicator == 'A'}?.LocDT
					def polDepartureDtE = firstOceanLeg.POL.DepartureDT.find{it.@Indicator == 'E'}?.LocDT
					if (polDepartureDtA) {
						'DTM' {
							'E374_01' '370'
							'E373_02' util.convertDateTime(polDepartureDtA.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
							'E337_03' util.convertDateTime(polDepartureDtA.text(), "yyyy-MM-dd'T'HH:mm:ss", 'HHmm')
							'E623_04' 'LT'
						}
					} else if (polDepartureDtE) {
						'DTM' {
							'E374_01' '369'
							'E373_02' util.convertDateTime(polDepartureDtE.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
							'E337_03' util.convertDateTime(polDepartureDtE.text(), "yyyy-MM-dd'T'HH:mm:ss", 'HHmm')
							'E623_04' 'LT'
						}
					}
				}
				
				'Loop_R4' {
					'R4' {
						'E115_01' 'D'
						if (lastPOD.Port.LocationCode.UNLocationCode) {
							'E309_02' 'UN'
						} else if (lastPOD.Port.LocationCode.SchedKDCode) {
							'E309_02' lastPOD.Port.LocationCode.SchedKDCode.text()
						}
						if (lastPOD.Port.LocationCode.UNLocationCode) {
							'E310_03' lastPOD.Port.LocationCode.UNLocationCode.text()
						} else if (lastPOD.Port.LocationCode.SchedKDCode) {
							'E310_03' lastPOD.Port.LocationCode.SchedKDCode.text()
						}
						if (lastPOD.Port.City) {
							'E114_04' util.substring(lastPOD.Port.City.text().toUpperCase(), 1, 24)
						}
						if (lastPOD.Port.CSCountryCode?.text().length() >= 2) {
							'E26_05' lastPOD.Port.CSCountryCode.text()
						}
						if (lastPOD.Facility.FacilityName) {
							'E174_06' lastPOD.Facility.FacilityName.text()
						}
						if (lastPOD.CSStateCode) {
							'E156_08' lastPOD.CSStateCode.text()
						}
					}
					def podArrivalDtA = lastOceanLeg.POD.ArrivalDT.find{it.@Indicator == 'A'}?.LocDT
					def podArrivalDtE = lastOceanLeg.POD.ArrivalDT.find{it.@Indicator == 'E'}?.LocDT
					if (podArrivalDtA) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(podArrivalDtA.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
							'E337_03' util.convertDateTime(podArrivalDtA.text(), "yyyy-MM-dd'T'HH:mm:ss", 'HHmm')
							'E623_04' 'LT'
						}
					} else if (podArrivalDtE) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(podArrivalDtE.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
							'E337_03' util.convertDateTime(podArrivalDtE.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
							'E623_04' 'LT'
						}
					}
					if( current_Body.BLGeneralInfo[0]?.CustomsReferenceNumber && current_Body.BLGeneralInfo[0].CustomsReferenceType?.text() =='IT'){
						'DTM' {
							'E374_01' '059'
							def lastOceanLegPodArrivalDtE = lastOceanLeg.POD?.ArrivalDT?.find{it.@Indicator == 'E'}?.LocDT
							if (lastOceanLegPodArrivalDtE) {
								'E373_02' util.convertDateTime(lastOceanLegPodArrivalDtE.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
								'E337_03' util.convertDateTime(lastOceanLegPodArrivalDtE.text(), "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMdd')
							}
							'E623_04' 'LT'
						}
					}
				}
				
				'Loop_R4' {
					'R4' {
						'E115_01' 'E'
						if (FND.CityDetails.LocationCode.UNLocationCode) {
							'E309_02' 'UN'
						} else if (FND.CityDetails.LocationCode.SchedKDCode) {
							'E309_02' FND.CityDetails.LocationCode.SchedKDType.text()
						}
						if (FND.CityDetails.LocationCode.UNLocationCode) {
							'E310_03' FND.CityDetails.LocationCode.UNLocationCode.text()
						}else if(FND.CityDetails.LocationCode.SchedKDCode) {
							'E310_03' FND.CityDetails.LocationCode.SchedKDCode.text()
						}
						if (FND.CityDetails.City) {
							'E114_04' util.substring(FND.CityDetails.City.text().toUpperCase(), 1, 24)
						}
						if (FND.CSStandardCity.CSContinentCode?.text().length() >= 2){
							'E26_05' FND.CSStandardCity.CSCountryCode.text()
						}
						if (FND.CSStandardCity.CSStateCode) {
							'E156_08' FND.CSStandardCity.CSStateCode.text()
						}
					}
				}
				'SE' {
					//SE-01 is auto line counter by BelugaOcean, so here need place a space is ok
					'E96_01' ' '
					'E329_02' currentSTCtrlNum
				}
			}
		}
		
		//GE
		'GE' {
			'E97_01' ContainerMovement?.Body?.size()
			'E28_02' ediGroupCtrlNum
		}
		//IEA
		'IEA' {
			'I16_01' '1'
			'I12_02' String.format("%09d", ediIsaCtrlNumber)
		}
	}
}

private boolean validateAfterExecution(){
	
}

private void beforeEnd(){

}

private buildBizKey(MarkupBuilder bizKeyXml, int current_BodyIndex, def current_Body,def util,Connection conn, def csuploadErrorKey, def Interchange, def scacTpId, def TP_ID, def eventExtCde) {
	
	def blBizKey = []
	def bkBizKey = []
	def crnBizKey = []
	def eventExtCdeBizKey = []
	def eventDateTimeBizKey = []
	def eventPortIdBizKey = []
	def eventPortNameBizKey = []
	def evnetCityIdBizKey = []
	def eventCityNameBizKey = []
	def evnetCntryCdeBizKey = []
	def partyType = ['ANP','BPT','CCP','CGN','FWD','NPT','OTH','SHP']

	current_Body.BLGeneralInfo.each { current_BLGeneralInfo ->
		if(util.notEmpty(current_BLGeneralInfo.BLNumber.text())){
			blBizKey.add(['BL_NUM':current_BLGeneralInfo.BLNumber.text()])
		}
	}
	current_Body.BookingGeneralInfo.each { current_BookingGeneralInfo ->
		if(util.notEmpty(current_BookingGeneralInfo.CarrierBookingNumber.text())){
			bkBizKey.add(['BK_NUM':current_BookingGeneralInfo.CarrierBookingNumber.text()])
		}
	}
	current_Body.Party.each { current_Party ->
		if(util.notEmpty(current_Party.CarrierCustomerCode.text()) && partyType.contains(current_Party.PartyType.text())){
			crnBizKey.add(['CRN':current_Party.CarrierCustomerCode.text()])
		}
	}
	if(util.notEmpty(eventExtCde)){
		eventExtCdeBizKey.add(['CT_Event_ext_cde':'eventExtCde'])
	}
	def eventInfo = getCS2EventInfo(current_Body.Event.Location.CityDetails.City.text(),current_Body.Event.Location.LocationCode.UNLocationCode.text(),current_Body.Event.Location.CSStandardCity.CSCountryCode.text(),conn)
	if(util.notEmpty(current_Body.Event.EventDT.LocDT.text())){
		eventDateTimeBizKey = ['CT_Event_datetime':util.convertDateTime(current_Body.Event.EventDT.LocDT.text(),"yyyy-MM-dd'T'HH:mm:ss", 'yyyy-MM-dd HH:mm:ss')]
	}
	if(util.notEmpty(current_Body.Event.EventDT.LocDT.text())){
		eventPortIdBizKey = ['CT_Event_port_id':eventInfo.CT_Event_port_id]
	}
	if(util.notEmpty(current_Body.Event.EventDT.LocDT.text())){
		eventPortNameBizKey = ['CT_Event_port_nme':eventInfo.CT_Event_port_nme]
	}
	if(util.notEmpty(current_Body.Event.EventDT.LocDT.text())){
		evnetCityIdBizKey = ['CT_Event_city_id':eventInfo.CT_Event_city_id]
	}
	if(util.notEmpty(current_Body.Event.EventDT.LocDT.text())){
		eventCityNameBizKey = ['CT_Event_city_nme':eventInfo.CT_Event_city_nme]
	}
	if(util.notEmpty(current_Body.Event.EventDT.LocDT.text())){
		evnetCntryCdeBizKey = ['CT_Event_cntry_cde':eventInfo.CT_Event_cntry_cde]
	}
	
	def bizKey = [
				['CNTR_NUM':current_Body.Container.ContainerNumber.text()],
				['STPID':scacTpId],['RTPID':TP_ID],
				['LKP':current_Body.GeneralInformation.SCAC.text() + ',' + current_Body.Container.ContainerNumber.text()  +','+ current_Body.Event.CS1Event.text() + ','+ util.convertDateTime(current_Body.Event.EventDT.LocDT.text(),"yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')],
			]
	bizKey.addAll(blBizKey)
	bizKey.addAll(bkBizKey)
	bizKey.addAll(crnBizKey)
	bizKey.addAll(eventExtCdeBizKey)
	bizKey.addAll(eventDateTimeBizKey)
	bizKey.addAll(eventPortIdBizKey)
	bizKey.addAll(eventPortNameBizKey)
	bizKey.addAll(evnetCityIdBizKey)
	bizKey.addAll(eventCityNameBizKey)
	bizKey.addAll(evnetCntryCdeBizKey)
	
	
	

	bizKeyXml.'ns0:Transaction'('xmlns:ns0':'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd'){
		'ns0:ControlNumberInfo'{
			'ns0:Interchange' Interchange
			'ns0:Group' String.format('%19s',current_Body.TransactionInformation.InterchangeTransactionID.text())?.replace(" ","0");
			'ns0:Transaction' current_BodyIndex + 1
		}
		bizKey.each{currentBizKeyMap ->
			currentBizKeyMap.each{key,value ->
				'ns0:BizKey'{
					'ns0:Type' key
					'ns0:Value' value
				}
			}
		}
		'ns0:CarrierId' getCarrierID(current_Body?.GeneralInformation?.SCAC?.text(),conn)
		'ns0:CTEventTypeId' current_Body.Event.CS1Event.text()
		def appErrorReportError = csuploadErrorKey.find{it.IsError == 'YES'}
		def appErrorReportObsolete = csuploadErrorKey.find{it.IsError == 'NO'}
		if(csuploadErrorKey.size != 0){
			'ns1:AppErrorReport'('xmlns:ns1':'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd'){
				if(appErrorReportError != null){
					'ns1:Status' 'E'
				}else{
					'ns1:Status' 'O'
				}
				'ns1:MsgCode' 'B2B-APP-MSG-GENERAL-01'
				if(appErrorReportError != null){
					'ns1:Msg' appErrorReportError?.Value
				}else{
					'ns1:Msg' appErrorReportObsolete?.Value
				}
				'ns1:Severity' '5'
			}
		}
	}
}

private buildCsupload(MarkupBuilder csuploadXml, List csuploadErrorKey,def Interchange,def msgId) {
	
	def currentSystemDt = new Date()
	
	csuploadXml.'ns0:CSUploadKeyTrack'('xmlns:ns0':'http://www.tibco.com/schemas/message-processing/Schemas/System/CSUploadKeyTrack.xsd'){
		'ns0:ErrorID' 'SEND_TO_TP'
		if(csuploadErrorKey.isEmpty()){
			'ns0:CSUploadErrorKey'{
				'ns0:Type' 'C'
				'ns0:Value' msgId + ".request"
				'ns0:IsError' 'NO'
			}
		}else{
			csuploadErrorKey.each{currentErrorKey ->
				'ns0:CSUploadErrorKey'{
					'ns0:Type' currentErrorKey?.Type
					'ns0:Value' currentErrorKey?.Value
					'ns0:IsError' currentErrorKey?.IsError
				}
			}
		}
		'ns0:CSUploadInfoKey'{
			'ns0:Type' 'SHMTQUEUEID'
			'ns0:Value' Interchange
		}
		'ns0:CSUploadInfoKey'{
			'ns0:Type' 'ACTIVITYSTARTDT'
			'ns0:Value' currentSystemDt.format("yyyyMMddHHmmss")
		}
	}
	
}

String getSCACTpId(String scac, Connection conn){
	if (conn == null)
		return "";

	String ret = "";
	PreparedStatement pre = null;
	ResultSet result = null;
	String sql = "select tp_id from b2b_scac_tp_map  where SCAC = ?";
	
	try {
		pre = conn.prepareStatement(sql);
//		pre.setMaxRows(getDBRowLimit());
//		pre.setQueryTimeout(getDBTimeOutInSeconds());
		pre.setMaxRows(1000);
		pre.setQueryTimeout(10);
		pre.setString(1, scac);
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

Map<String,String> getCS2EventInfo(String cityName, String unlocationCode, String countryCode,Connection conn){
	
	if (conn == null)
		return [:];

	def ret = [:];
	PreparedStatement pre = null;
	ResultSet result = null;
	PreparedStatement pre2 = null;
	ResultSet result2 = null;
	String sql1 = "select STND_PORT_ID, PORT_NAME from cs2_port_list where  (UPPER(port_name) = UPPER(?) or UN_LOCATION_CODE =?) and port_type ='S' and rownum = 1";
	String sql2 = "select stnd_city_id, city_nme, cntry_nme,cntry_cde, un_locn_cde from cs2_master_city where (UN_LOCN_CDE = ? or (UPPER(city_nme) = UPPER(?) and CNTRY_CDE = ? )) and city_type = 'S' and rownum=1";
	try {
		pre = conn.prepareStatement(sql1);
	//		pre.setMaxRows(getDBRowLimit());
	//		pre.setQueryTimeout(getDBTimeOutInSeconds());
		pre.setMaxRows(1000);
		pre.setQueryTimeout(10);
		pre.setString(1, cityName);
		pre.setString(2, unlocationCode);
		result = pre.executeQuery();
	
		if (result.next()) {
			ret.put('CT_Event_port_id',result.getString(1))
			ret.put('CT_Event_port_nme',result.getString(2))
		}
		
		pre2 = conn.prepareStatement(sql2);
		//		pre2.setMaxRows(getDBRowLimit());
		//		pre2.setQueryTimeout(getDBTimeOutInSeconds());
			pre2.setMaxRows(1000);
			pre2.setQueryTimeout(10);
			pre2.setString(1, unlocationCode);
			pre2.setString(2, cityName);
			pre2.setString(3, countryCode);
			result2 = pre2.executeQuery();
		
			if (result2.next()) {
				ret.put('CT_Event_city_id',result2.getString(1))
				ret.put('CT_Event_city_nme',result2.getString(2))
				ret.put('CT_Event_cntry_cde',result2.getString(4))
			}
		
	} finally {
		if (result != null)
			result.close();
		if (pre != null)
			pre.close();
		if (result2 != null)
			result2.close();
		if (pre2 != null)
			pre2.close();
	}
	return ret;
}

String getCarrierID(String scac, Connection conn){
	if (conn == null)
		return "";

	String ret = "";
	PreparedStatement pre = null;
	ResultSet result = null;
	String sql = "select c.id from b2b_ocean_carrier c where c.scac = ?";
	
	try {
		pre = conn.prepareStatement(sql);
//		pre.setMaxRows(getDBRowLimit());
//		pre.setQueryTimeout(getDBTimeOutInSeconds());
		pre.setMaxRows(1000);
		pre.setQueryTimeout(10);
		pre.setString(1, scac);
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











