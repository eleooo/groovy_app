package cs.b2b.mapping.scripts

import groovy.util.XmlSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/**
 * IG		: INTTRA VGM XML
 * Version	: 0.8
 */

String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {
	

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();

	/**
	 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
	 */
	inputXmlBody = util.removeBOM(inputXmlBody)
	
	/**
	 * Part II: get app mapping runtime parameters
	 */
	def appSessionId = util.getRuntimeParameter("AppSessionID", runtimeParams);
	def sourceFileName = util.getRuntimeParameter("OriginalSourceFileName", runtimeParams);
	//pmt info
	def TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams);
	def MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
	def DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams);
	def MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams);

	/**
	 * Part III: read xml and prepare output xml
	 */
	//Important: the inputXml is xml root element
	def SubmitVGM = new XmlParser().parseText(inputXmlBody);

	def writer = new StringWriter()
	def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
	outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
	

	/**
	 * Part IV: mapping script start from here
	 */

	TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
	def currentSystemDt = new Date()

	outXml.'ns0:CS2Manifest' ('xmlns:ns0':'http://www.cargosmart.com/eManifest/schemas/eManifest')
	{
		def varMessageGuid = SubmitVGM.MessageGuid.text()
		UUID idOne = UUID.randomUUID();		
		def OutUUID = MSG_REQ_ID + ',' + String.valueOf(idOne)
		def varAction = SubmitVGM.State.text()
		def varSCAC = util.getConversionByExtCdeWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'SCAC', SubmitVGM.Carrier?.PartyID?.ID?.text(),SubmitVGM.Carrier?.PartyID?.ID?.text(), conn)
		def varSenderID = TP_ID
		
		def varReceiver = util.getCarrierTpId(varSenderID, MSG_TYPE_ID, varSCAC, conn)
		
		
		
		'ns0:Header' {
			'ns1:ControlNumber' ('xmlns:ns1':'http://www.cargosmart.com/common', varMessageGuid)
			'ns1:MsgDT' ('xmlns:ns1':'http://www.cargosmart.com/common')
			{				
				'ns1:GMT' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss.Msz").replaceFirst('GMT', '') 
			} 
			'ns1:MsgDirection' ('xmlns:ns1':'http://www.cargosmart.com/common',DIR_ID)
			'ns1:MsgType' ('xmlns:ns1':'http://www.cargosmart.com/common',MSG_TYPE_ID)
			'ns1:SenderID' ('xmlns:ns1':'http://www.cargosmart.com/common',varSenderID)
			'ns1:ReceiverID' ('xmlns:ns1':'http://www.cargosmart.com/common','CARGOSMART')
			'ns1:Action' ('xmlns:ns1':'http://www.cargosmart.com/common','NEW')	
			'ns1:InterchangeMessageID' ('xmlns:ns1':'http://www.cargosmart.com/common',MSG_REQ_ID)
			'ns1:DataSource' ('xmlns:ns1':'http://www.cargosmart.com/common','B2B')		
		}
		
		// CS2Manifest.Body
		'ns0:Body' {
			// CS2Manifest.Body.TransactionInformation
			'ns0:TransactionInformation'
			{
				'ns1:MessageID' ('xmlns:ns1':'http://www.cargosmart.com/common',OutUUID)
				'ns1:GroupControlNumber' ('xmlns:ns1':'http://www.cargosmart.com/common',varMessageGuid)
				'ns1:InterchangeTransactionID'  ('xmlns:ns1':'http://www.cargosmart.com/common',varMessageGuid)		
			}			
			// CS2Manifest.Body.Manifest
			'ns0:Manifest'
			{				
				def varContainerNumber = SubmitVGM.ContainerNumber?.text()
				def varBillOfLadingNumber = SubmitVGM.BillOfLadingNumber?.text()
				def varBookingNumber = SubmitVGM.BookingNumber?.text()
				def varSubmitterReference = SubmitVGM.SubmitterReference?.text()

			//	def varFileCreationDatetime = Date.parse("yyyy-MM-dd'T'HH:mm:ss.Msz", SubmitVGM.MessageDateTime.text()).format("yyyyMMddHHmm")
			
				def date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(SubmitVGM.MessageDateTime.text().substring(0,18))
				def varFileCreationDatetime = new SimpleDateFormat("yyyyMMddHHmm").format(date)
//				2015-12-17T09:30:47.0Z
				// CS2Manifest.Body.Manifest.Header
				'ns0:Header' 
				{
					// CS2Manifest.Body.Manifest.Header.Organization
					'ns0:Organization'
					{
						if (varSCAC != "") {
							'ns0:Id' varSCAC
						}
						else {
							throw new Exception("Missing SCAC Code.")
						}
						'ns0:SuppRef'
						{
							'ns0:Qualifier' 'SENDER'
							'ns0:Ref'		'SPC'
						}
					}					
					'ns0:TestingInd' '0'
					'ns0:EdiMessageID' 'VGM'
					'ns0:CustomsID'
					{
						'ns0:Qualifier' 'Carrier'
						if (varSCAC != "") {
							'ns0:Value' varSCAC
						}
					}
					
					'ns0:FileCreationDatetime'
					{
						'ns1:dateStr' ('xmlns:ns1':'http://www.cargosmart.com/eManifest/schemas/eManifest/DataTypes',varFileCreationDatetime)
					}
					'ns0:ReferenceNum'
					{
						'ns0:Qualifier' 'Message'
						if (varMessageGuid != "") {
							'ns0:Value' varMessageGuid
						}
					}

					if (varSubmitterReference != "") {  
						'ns0:ReferenceNum'
						{
							'ns0:Qualifier' 'SI'
							'ns0:Value' varSubmitterReference
						}
					}
					switch(varAction) {
						case 'Original' : 'ns0:MessageFunction' '9'; break;
						case 'Amend' : 'ns0:MessageFunction' '5'; break;
						case 'Cancel' : 'ns0:MessageFunction' '1'; break;
						default : 'ns0:MessageFunction' '9'; break;
					}	
					if (varReceiver != "") {
						'ns0:Receiver' varReceiver
					}

				}
				// CS2Manifest.Body.Manifest.BLItemContainer				
				if (varBillOfLadingNumber != "") {
					'ns0:BLItemContainer'
					{
						'ns0:BLNum' varBillOfLadingNumber
						'ns0:ItemSeqNum' '1'
						if (varContainerNumber != "") {
							'ns0:ContainerNum' varContainerNumber
						}
					}						
	
				}
				// CS2Manifest.Body.Manifest.BK
				'ns0:BK'
				{
					def varSubmitterName = SubmitVGM.Submitter?.PartyName1?.text()
					def varResponsiblePartyName = SubmitVGM.ResponsibleParty?.PartyName1?.text()
					def varVerificationDetailsName = SubmitVGM.VerificationDetails?.PartyName1?.text()
					def varVerificationDeatilsSignature = SubmitVGM.VerificationDetails?.VerificationSignature?.text()
					def varWeighingPartyName = SubmitVGM.WeighingParty?.PartyName1?.text()
					def varAuthorizedPartyName = SubmitVGM.AuthorizedParty?.PartyName1?.text()
					def varCCC = util.getEDICdeRef(TP_ID, 'MapCCC', DIR_ID, 'VERMAS', 'DefaultCCC', varSCAC, 'DEFAULT',conn)
					// Map of Submitter Party				
					if (!SubmitVGM.Submitter.isEmpty()) {
						'ns0:Party'
						{
							'ns0:Type' 'SPC'
							'ns0:SeqNum' '1'
							if (varSubmitterName != "") {
								'ns0:Name' varSubmitterName
							}
							'ns0:SuppPartyID'
							{
								'ns0:Qualifier' 'PARTY_LEVEL'
								'ns0:PartyID' 'SHMT'
							}
						}
					}
					// Map of Responsible Party
					if (!SubmitVGM.ResponsibleParty.isEmpty()) {
						'ns0:Party'
						{
							'ns0:Type' 'SPC'
							'ns0:SeqNum' '1'
							if (varResponsiblePartyName != "") {
								'ns0:Name' varResponsiblePartyName
							}							
							'ns0:SuppPartyID'
							{
								'ns0:Qualifier' 'PARTY_LEVEL'
								'ns0:PartyID' 'CNTR'
							}
						}
					}
					// Map of Verification Party 
					if (!SubmitVGM.VerificationDetails.isEmpty()) {
						'ns0:Party'
						{
							'ns0:Type' 'AM'
							'ns0:SeqNum' '1'
							if (SubmitVGM.VerificationDetails?.Delegated?.text() == "false" && varResponsiblePartyName != "") {
								'ns0:Name' varResponsiblePartyName
							} else if (SubmitVGM.VerificationDetails?.Delegated?.text() == "true" && varAuthorizedPartyName != "") {
								'ns0:Name' varAuthorizedPartyName
							}
							if (varVerificationDeatilsSignature != "") {
								'ns0:ContactPerson'
								{
									'ns0:FirstName' varVerificationDeatilsSignature
								}	
							}
							'ns0:SuppPartyID'
							{
								'ns0:Qualifier' 'PARTY_LEVEL'
								'ns0:PartyID' 'CNTR'
							}
						}
					}
					// Map of Weighting Party
					if (!SubmitVGM.WeighingParty.isEmpty()) {
						'ns0:Party'
						{
							'ns0:Type' 'WPA'
							'ns0:SeqNum' '1'
							if (varWeighingPartyName != "") {
								'ns0:Name' varWeighingPartyName
							}
							'ns0:SuppPartyID'
							{
								'ns0:Qualifier' 'PARTY_LEVEL'
								'ns0:PartyID' 'CNTR'
							}
						}
					}
					// Map of Party type TB with Default CCC
					if (varCCC != "") {
						'ns0:Party'
						{
							'ns0:Type' 'TB'
							'ns0:SeqNum' '1'
							'ns0:SuppPartyID'
							{
								'ns0:Qualifier' 'WPA_INDEX'
								'ns0:PartyID' '0'
							}
							'ns0:SuppPartyID'
							{
								'ns0:Qualifier' 'PARTY_LEVEL'
								'ns0:PartyID' 'SHMT'
							}
							'ns0:SuppPartyID'
							{
								'ns0:Qualifier' 'CCC'
								'ns0:PartyID' varCCC
							}
						}
					}
					'ns0:Party'
					{
						'ns0:Type' 'EDI'
						'ns0:SeqNum' '1'
						if (TP_ID.substring(0, TP_ID.indexOf("_VGM")).trim() != "") {
							'ns0:Name' TP_ID.substring(0, TP_ID.indexOf("_VGM")).trim()
						}
/*						'ns0:SuppPartyID'
						{
							'ns0:Qualifier' 'PARTY_LEVEL'
							'ns0:PartyID' 'CNTR'
						}*/
					}
					if (varSCAC != "") {
						'ns0:Scac' varSCAC
					}
					if (varBookingNumber != "") {
						'ns0:BKNum' varBookingNumber
					}
				}
				// CS2Manifest.Body.Manifest.BKItemContainer
				if (varBookingNumber != "") {
					'ns0:BKItemContainer'
					{
						'ns0:BKNum' varBookingNumber
						'ns0:ItemSeqNum' '1'
						if (varContainerNumber != "") {
							'ns0:ContainerNum' varContainerNumber
						}
					}
				}
				// CS2Manifest.Body.Manifest.Container
				'ns0:Container'
				{
					if (varContainerNumber != "") {
						'ns0:ContainerNum' varContainerNumber
					}					
					def varVerifiedGrossMass = SubmitVGM.VerifiedGrossMass.Mass.text()
					if (varVerifiedGrossMass != "") {
						'ns0:Weight'
						{
							'ns0:Qualifier' 'GROSS'
							'ns0:Value' ('unit':util.getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, 'WeightQualifier', SubmitVGM.VerifiedGrossMass.UOM.text(), conn), varVerifiedGrossMass)
						}
					}
					def varISOContainerType = SubmitVGM.ContainerDetails.ISOContainerType.text()
					if (varISOContainerType != "") {
						'ns0:SizeType'
						{
							'ns0:Qualifier' 'ISO'
							'ns0:Value' varISOContainerType
						}
					}
					if (varSubmitterReference != "") {
						'ns0:SuppRef'
						{
							'ns0:Qualifier' 'SI'
							'ns0:Ref' varSubmitterReference
						}
					}
					//util.getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'AgeGroupBEG01-OLLXMLMsgStatus', currentDetail.BEG.BEG01.text(), 'NEW', conn)
					
					def varVGMDeterminationMethod = SubmitVGM.VGMDeterminationMethod.text()
					def varConvertMethod = util.getConversionByExtCde(TP_ID, MSG_TYPE_ID, DIR_ID, 'WeightMethod', varVGMDeterminationMethod, conn)
					if (varVGMDeterminationMethod != "") {
						'ns0:MiscInfo'
						{
							'ns0:Qualifier' 'WEIGHT_METHOD'
							if (varConvertMethod != "") {
								'ns0:Value' varConvertMethod
							}
						}
					}
					// For VGMXML Ack handling by different carriers			
					def varScacAck = util.getConversionWithScacByExtCde(null, MSG_TYPE_ID, DIR_ID, 'VERMAS_Ack_Handle', 'ContainerNumber', varSCAC, conn)
					
					if (varScacAck != "") {
						'ns0:MiscInfo'
						{
							'ns0:Qualifier' 'MsgId'
							'ns0:Value' OutUUID + varContainerNumber
						}				
					}
					'ns0:MiscInfo'
					{
						'ns0:Qualifier' 'WPA_INDEX'
						'ns0:Value' 1
					}

					def varVGMDeterminationDateTime = SubmitVGM.VGMDeterminationDateTime.text()
					
					if (varVGMDeterminationDateTime != "") {
						'ns0:WeightDate'
						{
							'ns1:GMT'  ('xmlns:ns1':'http://www.cargosmart.com/common',varVGMDeterminationDateTime)
						}	
					}
				}
			}			
		}
		
		def setMonResult = util.SetMonEDIControlNo(varSenderID, varReceiver, MSG_TYPE_ID, 'XML', varMessageGuid, varMessageGuid, varMessageGuid, OutUUID, MSG_REQ_ID, conn)
		def insertSCAC = util.InsertSCAC(MSG_REQ_ID, DIR_ID, 'OSCAC', varSCAC, conn)
		//def tmpAckKeyType = util.getConversionCommonWithScac(MSG_TYPE_ID, DIR_ID, 'VERMAS_Ack_Handle', varSCAC,conn)
		//def CheckAckKeyType = tmpAckKeyType.substring(0, tmpAckKeyType.indexOf(";")).trim()
		
		//if (CheckAckKeyType == "ContainerNumber"){
			
		//}
	}
	return writer.toString();
}