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
import java.util.List;
import java.util.Map;
import java.util.TimeZone

/**
 * IG		: INTTRA VGM XML
 * Version	: 0.8
 */

public class CUS_VGMXML_CS2ManifestXML_INTTRA {
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

	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {
		
	
		cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	
		/**
		 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
		 */
		inputXmlBody = util.removeBOM(inputXmlBody)
		
		/**
		 * Part II: get app mapping runtime parameters
		 */
		def appSessionId = util.getRuntimeParameter("B2BSessionID", runtimeParams);
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
		
		def bizKeywriter = new StringWriter()
		def bizKeyXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(bizKeywriter), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
		bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
		
		def bizKeyRoot = bizKeyXml.createNode('root')
		
		/**
		 * Part IV: mapping script start from here
		 */
	
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		def currentSystemDt = new Date()

		def varMessageGuid = SubmitVGM.MessageGuid.text()
		UUID idOne = UUID.randomUUID();
		def OutUUID = MSG_REQ_ID + ',' + String.valueOf(idOne)
		def varAction = SubmitVGM.State.text()
		def varSCAC = util.getConversionByExtCdeWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'SCAC', SubmitVGM.Carrier?.PartyID?.ID?.text(),SubmitVGM.Carrier?.PartyID?.ID?.text(), conn)
		def varSenderID = TP_ID
		
		def varReceiver = util.getCarrierTpId(varSenderID, MSG_TYPE_ID, varSCAC, conn)
	
		// For VGMXML Ack handling by different carriers
		def varScacAck = getScacAck(MSG_TYPE_ID, DIR_ID, 'VERMAS_Ack_Handle', 'ContainerNumber', varSCAC, conn)
		def varContainerNumber = SubmitVGM.ContainerNumber?.text()
		
		List<Map<String,String>> errorKeyList = prepChecking(varSenderID, varReceiver, MSG_TYPE_ID, varSCAC, SubmitVGM,  conn)
		
		if (errorKeyList.size()==0) {
			outXml.'ns0:CS2Manifest' ('xmlns:ns0':'http://www.cargosmart.com/eManifest/schemas/eManifest')
			{
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
						//def varContainerNumber = SubmitVGM.ContainerNumber?.text()
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
							
							
							if (varScacAck != "") {
								'ns0:MiscInfo'
								{
									'ns0:Qualifier' 'MsgId'
									'ns0:Value' OutUUID + "," +varContainerNumber
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
			}
		} else {
			outXml.'ns0:CS2Manifest' {}
		}
		
		buildBizKeyXML(bizKeyXml, MSG_REQ_ID, SubmitVGM, varSenderID, varReceiver,varMessageGuid, OutUUID, varSCAC, varScacAck, conn, errorKeyList)

		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		
		
		//promote bizkey to session
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_BIZKEY', bizKeywriter?.toString())
		
		def setMonResult = ""
		
		if (varScacAck != "") {
			setMonResult = util.SetMonEDIControlNo(varSenderID, varReceiver, MSG_TYPE_ID, 'XML', varMessageGuid, varMessageGuid, varMessageGuid, OutUUID + "," +varContainerNumber, MSG_REQ_ID, conn)
		} else setMonResult = util.SetMonEDIControlNo(varSenderID, varReceiver, MSG_TYPE_ID, 'XML', varMessageGuid, varMessageGuid, varMessageGuid, OutUUID, MSG_REQ_ID, conn)
		
		def insertSCAC = util.InsertSCAC(MSG_REQ_ID, DIR_ID, 'OSCAC', varSCAC, conn)
		
		String result = writer.toString();
		
		writer.close();
		bizKeywriter.close()
		
		return result
	}
	
	public String getScacAck(String MSG_TYPE_ID, String DIR_ID, String convertTypeId, String fromValue, String SCAC, Connection conn) throws Exception {
		if (conn == null)
			return "";
	
		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select int_cde from b2b_cde_conversion where tp_id is null and msg_type_id=? and dir_id=? and convert_type_id=? and ext_cde=? and scac_cde=?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10);
			pre.setQueryTimeout(10);
			pre.setString(1, MSG_TYPE_ID);
			pre.setString(2, DIR_ID);
			pre.setString(3, convertTypeId);
			pre.setString(4, fromValue);
			pre.setString(5, SCAC);
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
	
	private void buildBizKeyXML(MarkupBuilder bizKeyXml,  def MSG_REQ_ID, def SubmitVGM, def SenderID, def Receiver, def CT, def OutUUID, def SCAC, def ACKSCAC, Connection conn, def errorKeyList) {
		
		def appErrorReportError = errorKeyList.find{it?.IS_ERROR == YES}
		def appErrorReportObsolete = errorKeyList.find{it?.IS_ERROR == NO}
		
		def bizKey = [
					['BK_NUM': SubmitVGM.BookingNumber?.text()],
					['LKP':OutUUID],
					['CNTR_NUM':SubmitVGM.ContainerNumber?.text()],
					
					['STPID':SenderID],
					['RTPID':Receiver],
					['CT':CT],
				]
		
		
		
		if(checkE2EMon(SenderID+'_VGM',SenderID+'_VGM','L1P', conn)) {
			bizKey.add(['L1P':MSG_REQ_ID])
			bizKey.add(['L1STPID':SenderID+'_VGM'])
			bizKey.add(['L1RTPID':SenderID+'_VGM'])
		}
		
		
		if (checkE2EMon(SenderID,Receiver,'L3P', conn)) {
			bizKey.add(['L3STPID':SenderID])
			bizKey.add(['L3RTPID':Receiver])
			if (ACKSCAC!="") {
				bizKey.add(['L3P':OutUUID+","+SubmitVGM.ContainerNumber?.text()])
			} else bizKey.add(['L3P':OutUUID])
		}

		bizKeyXml.'ns0:Transaction'('xmlns:ns0': 'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {

			'ns0:ControlNumberInfo' {
				'ns0:Interchange' SubmitVGM.MessageGuid.text()
				'ns0:Group' SubmitVGM.MessageGuid.text()
				'ns0:Transaction' SubmitVGM.MessageGuid.text()
				}
			
			bizKey.each{ currentBizKeyMap ->
				currentBizKeyMap.each{ key, value ->
					'ns0:BizKey'{
						'ns0:Type' key
						'ns0:Value' value
					}
				}
			}
	
			'ns0:CarrierId' 
			'ns0:CTEventTypeId' 
			
			
			if (errorKeyList.size() != 0) {
					'ns1:AppErrorReport'('xmlns:ns1':'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd') {
					if (appErrorReportError != null) {
						'ns1:Status' ERROR
					} else {
						'ns1:Status' OBSOLETE
					}
					'ns1:MsgCode' 'B2B-APP-MSG-GENERAL'	//max length is 20, exceed will missing error bizkey
					
					def errMsg =""
					if (appErrorReportError != null) {
						errorKeyList.eachWithIndex {curErr, curErrIdx ->
							if(curErr.IS_ERROR==YES) {
								errMsg =errMsg + '[Error '+(curErrIdx+1)+' ]'+curErr.VALUE +', '
							}							
						}
					} else {
						errorKeyList.eachWithIndex {curErr, curErrIdx ->
							errMsg =errMsg + '[Obsolete '+(curErrIdx+1)+' ]'+curErr.VALUE +', '
						}
					}
				'ns1:Msg' errMsg.take(1000)
				'ns1:Severity' '5'
				}
			}

		}
	}
	
	public String checkE2EMon(def TPID, def Receiver, def MonType, Connection conn) {
		if (conn == null)
		return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select mon_type from b2b_e2e_pmt_cfg where sender_id=? and Receiver_id=? and start_msg_type_id='VGM' and mon_type=?";
	
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(10);
			pre.setQueryTimeout(10);
			pre.setString(1, TPID);
			pre.setString(2, Receiver);
			pre.setString(3, MonType);

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
	
	private List<Map<String,String>> prepChecking(def TPID, def ReceiverID, String msgType, String SCAC, def SubmitVGM, Connection conn) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();
		
		//check tp_integration_asso
		if (ReceiverID==null || ReceiverID=="") {
			Map<String,String> errorKey = null
			errorKey = errorKey = [TYPE: ERROR , IS_ERROR: YES,  VALUE: 'SCAC:' + SCAC + ' not integrate with customer']
			errorKeyList.add(errorKey);
		}
		
		//Container Number
		if (SubmitVGM.ContainerNumber?.text()=="") {
			Map<String,String> errorKey = null
			errorKey = errorKey = [TYPE: ERROR , IS_ERROR: YES,  VALUE: 'Container Number cannot be null']
			errorKeyList.add(errorKey);
		}
		
		//Booking Number
		if (SubmitVGM.BookingNumber?.text()=="" || SubmitVGM.BookingNumber?.text()==null) {
			Map<String,String> errorKey = null
			errorKey = errorKey = [TYPE: ERROR , IS_ERROR: YES,  VALUE: 'Missing Booking Number']
			errorKeyList.add(errorKey);
		}
		
		//Verified Gross Weight
		if (SubmitVGM.VerifiedGrossMass?.Mass?.text()=="" || SubmitVGM.VerifiedGrossMass?.UOM?.text()=="") {
			Map<String,String> errorKey = null
			errorKey = errorKey = [TYPE: ERROR , IS_ERROR: YES,  VALUE: 'Missing VGM Weight value or Unit under VerifiedGrossMass']
			errorKeyList.add(errorKey);
		}
		
		//Authorized Person and Responsible Party
		if (SubmitVGM.VerificationDetails?.VerificationSignature?.text()=="") {
			Map<String,String> errorKey = null
			errorKey = errorKey = [TYPE: ERROR , IS_ERROR: YES,  VALUE: 'Missing Authorized Person under VerificationDetails/VerificationSignature ']
			errorKeyList.add(errorKey);
		}
		
		
		return errorKeyList;
	}
}


