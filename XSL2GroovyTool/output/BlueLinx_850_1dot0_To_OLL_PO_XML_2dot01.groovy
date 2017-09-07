package b2b.pilot.groovy.scripts

import groovy.util.XmlSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

import javax.xml.xpath.*

import java.io.StringWriter
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date
import java.util.TimeZone
import java.util.jar.Attributes.Name;

//david 20160312

String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {

	//get OLL mapping runtime parameters, "OLLSessionID=aa", "OLL_OriginalSourceFileName=zz", "OLL_SendPortID=yy", "PortProperty=xx"
	def ollSessionId = getRuntimeParameter("OLLSessionID", runtimeParams);
	//	def sourceFileName = getRuntimeParameter("OLL_OriginalSourceFileName", runtimeParams);
	//	def portId = getRuntimeParameter("OLL_SendPortID", runtimeParams);
	//	def portProperty = getRuntimeParameter("PortProperty", runtimeParams);
	//pmt info
	def TP_ID = getRuntimeParameter("TP_ID", runtimeParams);
	def MSG_TYPE_ID = getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
	def DIR_ID = getRuntimeParameter("DIR_ID", runtimeParams);
	//	def MSG_REQ_ID = getRuntimeParameter("MSG_REQ_ID", runtimeParams);

	//Important: the inputXml is xml root element
	def inXml = new XmlParser().parseText(inputXmlBody);//XmlSlurper

	def writer = new StringWriter()
	def outXml = new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false)
	//def xmlBuilder = new StreamingMarkupBuilder()

	//mapping script

	TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
	def currentSystemDt = new Date()
	def current_dateTime = currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")

	def uniqueId = inXml.ECOM_Header.Unique_ID.text()
	if (uniqueId!=null && uniqueId.length()>=9) {
		uniqueId = uniqueId.substring(uniqueId.length()-9)
	}

	outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

	//mapping logic start
	outXml.'ns0:po_file_root' ('xmlns:ns0':'http://www.oocllogistics.com/po', 'xmlns:datatype':'http://www.oocllogistics.com/datatype',
	'xmlns:notelog':'http://www.oocllogistics.com/notelog', 'xmlns:header':'http://www.oocllogistics.com/header',
	'xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance')
	{
		'ns0:file_header' {
			'header:SENDER_ID' 'BLUELINX'
			'header:RECIPIENT_ID' 'POP'
			'header:MSG_CTRL_REF_NUM' inXml.ISA.ControlNumber.text()
			'header:MSG_SENT_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
			'header:MSG_REC_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
			'header:MSG_STATUS' 'N'
			'header:MSG_TYPE' 'PO'
			'header:MSG_DEF_VER' '2.01'
		}
		inXml.Transaction.each{currentTransaction ->
			'ns0:po_msg_root' {
				'ns0:msg_header' {
					'header:SENDER_ID' 'BLUELINX'
					'header:RECIPIENT_ID' 'POP'
					'header:MSG_CTRL_REF_NUM' currentTransaction.ST.ControlNumber.text()
					'header:MSG_SENT_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
					'header:MSG_STATUS' getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'BluelinxBEG01-OLLMsgStatus', currentTransaction.BEG.PurposeCode.text(), 'N', conn)
					'header:MSG_TYPE' 'PO'
					'header:MSG_DEF_VER' '2.01'
					'header:data_header' {
						'header:to' 'POP'
						'header:from' 'BLUELINX'
						'header:time_stamp' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
						'header:header_msg_type' 'PO'
						'header:body_msg_type' 'PO'
						'header:msg_ver' '2.01'
					}
				}
				'ns0:ext_po_num' currentTransaction.BEG.PONumber.text()
				'ns0:po_num' currentTransaction.BEG.PONumber.text()
				//				def CNNode = currentTransaction.N1_Loop.findAll{it.'N1'.'IdentityCode'.text()=='CN'}
				def CNNode = null;	//oll bug, CNNode always is null, above one is correct.
				def BTNode = currentTransaction.N1_Loop.findAll{it.'N1'.'IdentityCode'.text()=='BT'}
				def sDeptNode = currentTransaction.REF.findAll{it.ReferenceIdenQualifier.text()=='6S'}
				if(notEmpty(CNNode)){
					'ns0:cust' {
						if(notEmpty(CNNode.N1.Name[0].text())){
							'datatype:PARTNER_NAME' CNNode.N1.Name[0].text()
						}
						if(notEmpty(CNNode.N1.Name[0].text())){
							'datatype:PARTNER_EXT_REF_NUM' CNNode.N1.Name[0].text()
						}
						'datatype:ADDR' {
							if(notEmpty(CNNode.N4.N402_StateProvinceCode[0].text())){
								'datatype:state_name' CNNode.N4.N402_StateProvinceCode[0].text()
							}
							'datatype:city'{
								if(notEmpty(CNNode.N4.N401_CityName[0].text())){
									'datatype:LOC_NAME' CNNode.N4.N401_CityName[0].text()
								}
								if(notEmpty(CNNode.N4.N404_CountryCode[0].text())){
									'datatype:COUNTRY_NAME' CNNode.N4.N404_CountryCode[0].text()
								}
								if(notEmpty(CNNode.N4.N404_CountryCode[0].text())){
									'datatype:country_code' CNNode.N4.N404_CountryCode[0].text()
								}
							}
							if(notEmpty(CNNode.N4.N403_PostalCode[0].text())){
								'datatype:postal_code' CNNode.N4.N403_PostalCode[0].text()
							}
							'datatype:ADDR_LINE' CNNode.N3.Name[0].text()
							if(notEmpty(CNNode.N3.Name2[0].text())){
								'datatype:ADDR_LINE' CNNode.N3.Name2[0].text()
							}
						}
					}
					'ns0:csgn' {
						if(notEmpty(CNNode.N1.Name[0].text())){
							'datatype:PARTNER_NAME' CNNode.N1.Name[0].text()
						}
						if(notEmpty(CNNode.N1.Name[0].text())){
							'datatype:PARTNER_EXT_REF_NUM' CNNode.N1.Name[0].text()
						}
						'datatype:ADDR' {
							if(notEmpty(CNNode.N4.N402_StateProvinceCode[0].text())){
								'datatype:state_name' CNNode.N4.N402_StateProvinceCode[0].text()
							}
							'datatype:city'{
								if(notEmpty(CNNode.N4.N401_CityName[0].text())){
									'datatype:LOC_NAME' CNNode.N4.N401_CityName[0].text()
								}
								if(notEmpty(CNNode.N4.N404_CountryCode[0].text())){
									'datatype:COUNTRY_NAME' CNNode.N4.N404_CountryCode[0].text()
								}
								if(notEmpty(CNNode.N4.N404_CountryCode[0].text())){
									'datatype:country_code' CNNode.N4.N404_CountryCode[0].text()
								}
							}
							if(notEmpty(CNNode.N4.N403_PostalCode[0].text())){
								'datatype:postal_code' CNNode.N4.N403_PostalCode[0].text()
							}
							'datatype:ADDR_LINE' CNNode.N3.Name[0].text()
							if(notEmpty(CNNode.N3.Name2[0].text())){
								'datatype:ADDR_LINE' CNNode.N3.Name2[0].text()
							}
						}
						if(notEmpty(sDeptNode)){
							'ns0:dept' sDeptNode[0].ReferenceIden.text()
						}
					}
				}else{
					'ns0:cust' {
						if(notEmpty(BTNode.N1.Name[0].text())){
							'datatype:PARTNER_NAME' BTNode.N1.Name[0].text()
						}
						if(notEmpty(BTNode.N1.Name[0].text())){
							'datatype:PARTNER_EXT_REF_NUM' BTNode.N1.Name[0].text()
						}
						'datatype:ADDR' {
							if(notEmpty(BTNode.N4.N402_StateProvinceCode[0].text())){
								'datatype:state_name' BTNode.N4.N402_StateProvinceCode[0].text()
							}
							'datatype:city'{
								if(notEmpty(BTNode.N4.N401_CityName[0].text())){
									'datatype:LOC_NAME' BTNode.N4.N401_CityName[0].text()
								}
								if(notEmpty(BTNode.N4.N404_CountryCode[0].text())){
									'datatype:COUNTRY_NAME' BTNode.N4.N404_CountryCode[0].text()
								}
								if(notEmpty(BTNode.N4.N404_CountryCode[0].text())){
									'datatype:country_code' BTNode.N4.N404_CountryCode[0].text()
								}
							}
							if(notEmpty(BTNode.N4.N403_PostalCode[0].text())){
								'datatype:postal_code' BTNode.N4.N403_PostalCode[0].text()
							}
							if(notEmpty(BTNode.N3.Name[0].text())){
								'datatype:ADDR_LINE' BTNode.N3.Name[0].text()
							}
							if(notEmpty(BTNode.N3.Name2[0].text())){
								'datatype:ADDR_LINE' BTNode.N3.Name2[0].text()
							}
						}
					}
					'ns0:csgn' {
						if(notEmpty(BTNode.N1.Name[0].text())){
							'datatype:PARTNER_NAME' BTNode.N1.Name[0].text()
						}
						if(notEmpty(BTNode.N1.Name[0].text())){
							'datatype:PARTNER_EXT_REF_NUM' BTNode.N1.Name[0].text()
						}
						'datatype:ADDR' {
							if(notEmpty(BTNode.N4.N402_StateProvinceCode[0].text())){
								'datatype:state_name' BTNode.N4.N402_StateProvinceCode[0].text()
							}
							'datatype:city'{
								if(notEmpty(BTNode.N4.N401_CityName[0].text())){
									'datatype:LOC_NAME' BTNode.N4.N401_CityName[0].text()
								}
								if(notEmpty(BTNode.N4.N404_CountryCode[0].text())){
									'datatype:COUNTRY_NAME' BTNode.N4.N404_CountryCode[0].text()
								}
								if(notEmpty(BTNode.N4.N404_CountryCode[0].text())){
									'datatype:country_code' BTNode.N4.N404_CountryCode[0].text()
								}
							}
							if(notEmpty(BTNode.N4.N403_PostalCode[0].text())){
								'datatype:postal_code' BTNode.N4.N403_PostalCode[0].text()
							}
							'datatype:ADDR_LINE' BTNode.N3.Name[0].text()
							if(notEmpty(BTNode.N3.Name2[0].text())){
								'datatype:ADDR_LINE' BTNode.N3.Name2[0].text()
							}
						}
						if(notEmpty(sDeptNode)){
							'ns0:dept' sDeptNode[0].ReferenceIden.text()
						}
					}
				}
				currentTransaction.PER.each{currentPER ->
					if(currentPER.PER01.text() == 'BD'){
						'ns0:byr'{
							if(notEmpty(currentPER.PER02.text())){
								'datatype:ADDR_LINE' currentPER.PER02.text()
							}
							'datatype:ADDR' {
								if(notEmpty(currentPER.PER04.text())){
									String[] phoneParts = currentPER.PER04.text().split("( |-)")
									String[] sParts = currentPER.PER04.text().split("-")
									'datatype:CONTACT'{
										'datatype:CONTACT_TYPE' 'PHONE'
										'datatype:country_code' (phoneParts.length > 2) ? phoneParts[phoneParts.length - 3] : null
										'datatype:area_code' (phoneParts.length > 1) ? phoneParts[phoneParts.length - 2] : null
										'datatype:CONTACT_NUM' sParts[sParts.length - 1]
									}
								}
								if(notEmpty(currentPER.PER06.text())){
									'datatype:CONTACT'{
										'datatype:CONTACT_TYPE' 'EMAIL'
										'datatype:email' currentPER.PER06.text()
									}
								}
							}
						}
					}
				}
				currentTransaction.N1_Loop.each { currentN1Loop ->
					if(currentN1Loop.N1.IdentityCode.text() == 'MP'){
						'ns0:mfr' {
							if(notEmpty(currentN1Loop.N1.Name.text())){
								'datatype:PARTNER_NAME' currentN1Loop.N1.Name.text()
							}
							'datatype:ADDR' {
								if(notEmpty(currentN1Loop.N4.N402_StateProvinceCode.text())){
									'datatype:state_name' currentN1Loop.N4.N402_StateProvinceCode.text()
								}
								'datatype:city'{
									'datatype:LOC_NAME' currentN1Loop.N4.N401_CityName.text()
									if(notEmpty(currentN1Loop.N4.N404_CountryCode.text())){
										'datatype:COUNTRY_NAME' currentN1Loop.N4.N404_CountryCode.text()
									}
									if(notEmpty(currentN1Loop.N4.N404_CountryCode.text())){
										'datatype:country_code' currentN1Loop.N4.N404_CountryCode.text()
									}
								}
								if(notEmpty(currentN1Loop.N4.N403_PostalCode.text())){
									'datatype:postal_code' currentN1Loop.N4.N403_PostalCode.text()
								}
								'datatype:ADDR_LINE' currentN1Loop.N3.Name.text()
								if(notEmpty(currentN1Loop.N3.Name2.text())){
									'datatype:ADDR_LINE' currentN1Loop.N3.Name2.text()
								}
								'datatype:ADDR_LINE' currentN1Loop.N4.N401_CityName.text() + (notEmpty(currentN1Loop.N4.N402_StateProvinceCode.text()) ? ',' + currentN1Loop.N4.N402_StateProvinceCode.text() : '') + (notEmpty(currentN1Loop.N4.N403_PostalCode.text()) ? ',' + currentN1Loop.N4.N403_PostalCode.text() : '') + (notEmpty(currentN1Loop.N4.N404_CountryCode.text()) ? ',' +currentN1Loop.N4.N404_CountryCode.text() : '')
							}
						}
					}
				}
				currentTransaction.N1_Loop.each { currentN1Loop ->
					if(currentN1Loop.N1.IdentityCode.text() == 'SF'){
						'ns0:vdr' {
							if(notEmpty(currentN1Loop.N1.Name.text())){
								'datatype:PARTNER_NAME' currentN1Loop.N1.Name.text()
							}
							if(notEmpty(currentN1Loop.N1.Name.text())){
								'datatype:PARTNER_EXT_REF_NUM' currentN1Loop.N1.Name.text()
							}
							'datatype:ADDR' {
								'datatype:country_code' notEmpty(currentN1Loop.N4.N404_CountryCode.text()) ? currentN1Loop.N4.N404_CountryCode.text() : null
								'datatype:country_name' notEmpty(notEmpty(currentN1Loop.N4.N404_CountryCode.text())) ? currentN1Loop.N4.N404_CountryCode.text() : null
								'datatype:state_name' notEmpty(currentN1Loop.N4.N402_StateProvinceCode.text()) ? currentN1Loop.N4.N402_StateProvinceCode.text() : null
								'datatype:city'{
									'datatype:LOC_NAME' currentN1Loop.N4.N401_CityName.text()
								}
								'datatype:postal_code' notEmpty(currentN1Loop.N4.N403_PostalCode.text()) ? currentN1Loop.N4.N403_PostalCode.text() : null
								'datatype:ADDR_LINE' currentN1Loop.N3.Name.text()
								'datatype:ADDR_LINE' notEmpty(currentN1Loop.N3.Name2.text()) ? currentN1Loop.N3.Name2.text() : null
								'datatype:ADDR_LINE' currentN1Loop.N4.N401_CityName.text()
								'datatype:ADDR_LINE' notEmpty(currentN1Loop.N4.N402_StateProvinceCode.text()) ? currentN1Loop.N4.N402_StateProvinceCode.text() : null
								'datatype:ADDR_LINE' currentN1Loop.N4.N401_CityName.text() + (notEmpty(currentN1Loop.N4.N402_StateProvinceCode.text()) ? ',' + currentN1Loop.N4.N402_StateProvinceCode.text() : '') + (notEmpty(currentN1Loop.N4.N403_PostalCode.text()) ? ',' + currentN1Loop.N4.N403_PostalCode.text() : '') + (notEmpty(currentN1Loop.N4.N404_CountryCode.text()) ? ',' +currentN1Loop.N4.N404_CountryCode.text() : '')
							}
						}
					}
				}
				currentTransaction.N1_Loop.each { currentN1Loop ->
					def billToQualifier = (currentTransaction.BEG.PONumber.text().startsWith("CCP")) ? 'ST' : 'FS'
					if(billToQualifier == currentN1Loop.N1.IdentityCode.text()){
						'ns0:billto' {
							if(notEmpty(currentN1Loop.N1.Name.text())){
								'datatype:PARTNER_NAME' currentN1Loop.N1.Name.text()
							}
							if(notEmpty(currentN1Loop.N1.Name.text())){
								'datatype:PARTNER_EXT_REF_NUM' currentN1Loop.N1.Name.text()
							}
							'datatype:ADDR' {
								'datatype:city'{
									'datatype:LOC_NAME' currentN1Loop.N4.N401_CityName.text()
									if(notEmpty(currentN1Loop.N4.N404_CountryCode.text())){
										'datatype:COUNTRY_NAME' currentN1Loop.N4.N404_CountryCode.text()
									}
									if(notEmpty(currentN1Loop.N4.N404_CountryCode.text())){
										'datatype:country_code' currentN1Loop.N4.N404_CountryCode.text()
									}
								}
								if(notEmpty(currentN1Loop.N4.N403_PostalCode.text())){
									'datatype:postal_code' currentN1Loop.N4.N403_PostalCode.text()
								}
								'datatype:ADDR_LINE' currentN1Loop.N3.Name.text()
								if(notEmpty(currentN1Loop.N3.Name2.text())){
									'datatype:ADDR_LINE' currentN1Loop.N3.Name2.text()
								}
								'datatype:ADDR_LINE' currentN1Loop.N4.N401_CityName.text() + (notEmpty(currentN1Loop.N4.N402_StateProvinceCode.text()) ? ',' + currentN1Loop.N4.N402_StateProvinceCode.text() : '') + (notEmpty(currentN1Loop.N4.N403_PostalCode.text()) ? ',' + currentN1Loop.N4.N403_PostalCode.text() : '') + (notEmpty(currentN1Loop.N4.N404_CountryCode.text()) ? ',' +currentN1Loop.N4.N404_CountryCode.text() : '')
							}
						}
					}
				}
				'ns0:po_attr' {
					'datatype:data_status_code' getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'BluelinxBEG01-OLLDataStatusCode', currentTransaction.BEG.PurposeCode.text(), 'STATUS_ACTIVE', conn)
					'datatype:crt_date' {
						'datatype:record_datetime' convertDateTime(currentTransaction.BEG.Date.text(), 'yyyyMMdd', 'yyyy-MM-dd\'T00:00:00.0\'')
					}
					'datatype:issue_date' {
						'datatype:record_datetime' convertDateTime(currentTransaction.BEG.Date.text(), 'yyyyMMdd', 'yyyy-MM-dd\'T00:00:00.0\'')
					}
				}
				'ns0:payment_method'{
					'datatype:payment_method_code' ("CC" == currentTransaction.FOB.FOB01.text() ? 'C' : 'P')
					'datatype:ext_ref' currentTransaction.FOB.FOB01.text()
				}
				currentTransaction.TD5.each{currentTD5 ->
					if(currentTD5.td504.text() == 'ZZ'){
						'ns0:ship_via' {
							'datatype:ship_via_code' getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'BlueLinxShipVia-OLLShipVia', currentTD5.td505.text(), 'S', conn)
							if(notEmpty(currentTD5.td505.text())){
								'datatype:ship_via_ext_ref' currentTD5.td505.text()
							}
						}
					}
				}
				currentTransaction.TD5.each{currentTD5 ->
					if(currentTD5.TransTypeCode.text() == '58'){
						'ns0:por' {
							if(notEmpty(currentTD5.td505.text())){
								'datatype:LOC_NAME' currentTD5.td505.text()
							}
							if(notEmpty(currentTD5.Routing.text())){
								'datatype:UNLOCODE' currentTD5.Routing.text()
								'datatype:cust_ref' currentTD5.Routing.text()
							}
						}
					}
				}
				currentTransaction.TD5.each{currentTD5 ->
					if(currentTD5.TransTypeCode.text() == '58'){
						'ns0:pol' {
							if(notEmpty(currentTD5.td505.text())){
								'datatype:LOC_NAME' currentTD5.td505.text()
							}
							if(notEmpty(currentTD5.Routing.text())){
								'datatype:UNLOCODE' currentTD5.Routing.text()
								'datatype:cust_ref' currentTD5.Routing.text()
							}
						}
					}
				}
				currentTransaction.TD5.each{currentTD5 ->
					if(currentTD5.TransTypeCode.text() == '59'){
						'ns0:pod' {
							if(notEmpty(currentTD5.td505.text())){
								'datatype:LOC_NAME' currentTD5.td505.text()
							}
							if(notEmpty(currentTD5.Routing.text())){
								'datatype:UNLOCODE' currentTD5.Routing.text()
								'datatype:cust_ref' currentTD5.Routing.text()
							}
						}
					}
				}
				currentTransaction.N1_Loop.each { currentN1Loop ->
					if(currentN1Loop.N1.IdentityCode.text() == 'BT'){
						'ns0:fnd' {
							'datatype:LOC_NAME' currentN1Loop.N4.N401_CityName.text()
							'datatype:cust_ref' currentN1Loop.N4.N401_CityName.text()
						}
					}
				}
				'ns0:ship_window' {
					currentTransaction.DTM.each{currentDTM->
						if(currentDTM.DTM01.text() == '017'){
							'datatype:earliest_pol_etd_date'{
								'datatype:record_datetime'  convertDateTime(currentDTM.DTM02.text(),'yyyyMMdd',"yyyy-MM-dd'T'HH:mm:ss.S")
							}
						}
					}
					currentTransaction.DTM.each{currentDTM->
						if(currentDTM.DTM01.text() == '010'){
							'datatype:target_pol_etd_date'{
								'datatype:record_datetime'  convertDateTime(currentDTM.DTM02.text(),'yyyyMMdd',"yyyy-MM-dd'T'HH:mm:ss.S")
							}
						}
					}
					currentTransaction.DTM.each{currentDTM->
						if(currentDTM.DTM01.text() == '038'){
							'datatype:latest_pol_etd_date'{
								'datatype:record_datetime'  convertDateTime(currentDTM.DTM02.text(),'yyyyMMdd',"yyyy-MM-dd'T'HH:mm:ss.S")
							}
						}
					}
					currentTransaction.DTM.each{currentDTM->
						if(currentDTM.DTM01.text() == '002'){
							'datatype:fnd_eta_date'{
								'datatype:record_datetime'  convertDateTime(currentDTM.DTM02.text(),'yyyyMMdd',"yyyy-MM-dd'T'HH:mm:ss.S")
							}
						}
					}
				}
				currentTransaction.REF.each{currentREF ->
					if(currentREF.ReferenceIdenQualifier.text() == "FT"){
						'ns0:incoterm'{
							'datatype:intcoterm_code' getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'Bluelinx_REF_FT_OLLIncotermCode', currentREF.ReferenceIden.text(), null, conn)
							if(notEmpty(currentREF.REF03.text())){
								'datatype:intcoterm_name' currentREF.REF03.text()
							}
							if(notEmpty(currentREF.ReferenceIden.text())){
								'datatype:intcoterm_ext_ref' currentREF.ReferenceIden.text()
							}
						}
					}

				}
				if(notEmpty(currentTransaction.ITD.ITD02.text())){
					'ns0:payment_term' currentTransaction.ITD.ITD02.text()
				}
				currentTransaction.N1_Loop.each{currentN1Loop ->
					def shipToQualifier = (currentTransaction.BEG.PONumber.text().startsWith("CCP")) ? 'FS' : 'ST'
					if(currentN1Loop.N1.IdentityCode.text() == shipToQualifier){
						'ns0:ship_to'{
							if(notEmpty(currentN1Loop.N1.Name.text())){
								'datatype:PARTNER_NAME' currentN1Loop.N1.Name.text()
							}
							'datatype:ADDR' {
								if(notEmpty(currentN1Loop.N4.N402_StateProvinceCode.text())){
									'datatype:state_name' currentN1Loop.N4.N402_StateProvinceCode.text()
								}
								'datatype:city'{
									'datatype:LOC_NAME' currentN1Loop.N4.N401_CityName.text()
									if(notEmpty(currentN1Loop.N4.N404_CountryCode.text())){
										'datatype:COUNTRY_NAME' currentN1Loop.N4.N404_CountryCode.text()
									}
									if(notEmpty(currentN1Loop.N4.N404_CountryCode.text())){
										'datatype:country_code' currentN1Loop.N4.N404_CountryCode.text()
									}

								}
								if(notEmpty(currentN1Loop.N4.N403_PostalCode.text())){
									'datatype:postal_code' currentN1Loop.N4.N403_PostalCode.text()
								}
								'datatype:ADDR_LINE' currentN1Loop.N3.Name.text()
								if(notEmpty(currentN1Loop.N3.Name2.text())){
									'datatype:ADDR_LINE' currentN1Loop.N3.Name2.text()
								}
								'datatype:ADDR_LINE' currentN1Loop.N4.N401_CityName.text() + (notEmpty(currentN1Loop.N4.N402_StateProvinceCode.text()) ? ',' + currentN1Loop.N4.N402_StateProvinceCode.text() : '') + (notEmpty(currentN1Loop.N4.N403_PostalCode.text()) ? ',' + currentN1Loop.N4.N403_PostalCode.text() : '') + (notEmpty(currentN1Loop.N4.N404_CountryCode.text()) ? ',' +currentN1Loop.N4.N404_CountryCode.text() : '')
								String[] phoneParts = currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='FS'}[0].N2.N201.text().split("( |-)")
								String[] sParts = currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='FS'}[0].N2.N201.text().split("-")
								'datatype:CONTACT'{
									'datatype:CONTACT_TYPE' 'PHONE'
									'datatype:country_code' (phoneParts.length > 2 ? phoneParts[phoneParts.length - 3] : null)
									'datatype:area_code' (phoneParts.length > 1 ? phoneParts[phoneParts.length - 2] : null)
									'datatype:CONTACT_NUM' sParts[sParts.length - 1]
								}
							}
						}
					}
				}
				currentTransaction.PO1_Loop.each{currentPO1Loop ->
					'ns0:po_item'{
						'datatype:sku'{
							if(notEmpty(currentPO1Loop.PO1.ServiceID.text())){
								'datatype:STYLE_CODE' currentPO1Loop.PO1.ServiceID.text()
							}
							if(notEmpty(currentPO1Loop.PO1.AssignedIdentification.text())){
								'datatype:COLOR_CODE' currentPO1Loop.PO1.AssignedIdentification.text()
							}
							currentPO1Loop.REF.each {currentREF ->
								if(currentREF.ReferenceIdenQualifier.text() == 'DT'){
									if(notEmpty(currentREF.ReferenceIden.text())){
										'datatype:SIZE_CODE' currentREF.ReferenceIden.text()
									}
								}
							}
							'datatype:DESC' currentPO1Loop.PID_Loop[0].PID[0].Description.text()
						}
						'datatype:sku_measurement'{
							if(notEmpty(currentPO1Loop.PO4.PO406.text())){
								'datatype:wgt' currentPO1Loop.PO4.PO406.text()
							}
						}
						'datatype:outer_uom'{ 'datatype:uom_ext_ref' 'PKG' }
						currentPO1Loop.CTP.each {currentCTP ->
							if(currentCTP.CTP02.text() == 'NET' && notEmpty(currentCTP.CTP04.text())){
								'datatype:outer_uom_qty'  currentCTP.CTP04.text()
							}
						}
						'datatype:lowest_uom'{
							'datatype:UOM_CODE' getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'BlueLinxUOMCode-OLLStandardUOMCode', currentPO1Loop.PO1.UnitBasisCode.text(), currentPO1Loop.PO1.UnitBasisCode.text(), conn)
							if(notEmpty(currentPO1Loop.PO1.UnitBasisCode.text())){
								'datatype:uom_ext_ref' currentPO1Loop.PO1.UnitBasisCode.text()
							}
						}
						if(notEmpty(currentPO1Loop.PO1.Quantity.text())){
							'datatype:lowest_uom_qty' currentPO1Loop.PO1.Quantity.text()
						}
						'ns0:ship_windows'{
							currentPO1Loop.REF.each {currentREF ->
								if(currentREF.ReferenceIdenQualifier.text() == 'DT'){
									'datatype:target_pol_etd_date'{
										'datatype:record_datetime' convertDateTime(currentREF.ReferenceIdenQualifier.text(),'yyyyMMdd',"yyyy-MM-dd'T'HH:mm:ss.S")
									}
								}
							}
						}
						'ns0:item_attr'{
							'datatype:data_status_code' ('xsi:nil':"true")
							currentPO1Loop.REF.each {currentREF ->
								if(currentREF.ReferenceIdenQualifier.text() == 'DT'){
									'datatype:cancel_date'{
										'datatype:record_datetime' convertDateTime(currentREF.ReferenceIdenQualifier.text(),'yyyyMMdd',"yyyy-MM-dd'T'HH:mm:ss.S")
									}
								}
							}
						}
						currentPO1Loop.MEA.each{currentMEA->
							if(currentMEA.MEA01.text() == 'LD' && notEmpty(currentMEA.MEA03.text())){
								'ns0:lowest_uom_price' {
									'datatype:cur_code' ('xsi:nil':'true')
									'datatype:amt' currentMEA.MEA03.text()
								}
							}
						}
						currentPO1Loop.CTP.each{currentCTP ->
							if(currentCTP.CTP02.text() == 'NET' && notEmpty(currentCTP.CTP08.text())){
								'ns0:est_lowest_uom_cost'{
									'datatype:cur_code' ('xsi:nil':'true')
									'datatype:amt' currentCTP.CTP08.text()
								}
							}
						}
						'ns0:customized_field'{
							'datatype:field_code' 'PO_DETAIL_GF_03'
							'datatype:field_value' currentPO1Loop.PCT.PCT02.text()
						}
						'ns0:customized_field'{
							'datatype:field_code' 'PO_DETAIL_GF_02'
							'datatype:field_value' notEmpty(currentTransaction.PO1_Loop.CTP.findAll{it.CTP02.text()=='NET' && notEmpty(it.CTP03.text())}) ? currentTransaction.PO1_Loop.CTP.findAll{it.CTP02.text()=='NET'}[0].CTP03.text() : null
							//							'datatype:field_value' (currentTransaction.PO1_Loop.CTP.findAll{it.CTP02.text()=='NET'})[0].CTP03.text()
						}
						'ns0:customized_field'{
							'datatype:field_code' 'PO_DETAIL_GF_01'
							'datatype:field_value' notEmpty(currentTransaction.PO1_Loop.PO1.ServiceID2.text()) ? currentTransaction.PO1_Loop.PO1.ServiceID2.text() : null
						}
						'ns0:customized_field'{
							'datatype:field_code' 'PO_DETAIL_GF_04'
							'datatype:field_value' notEmpty(currentTransaction.PO1_Loop.CTP.findAll{it.CTP02.text()=='NET' && notEmpty(it.CTP04.text())}) ? currentTransaction.PO1_Loop.CTP.findAll{it.CTP02.text()=='NET'}[0].CTP04.text() : null
						}
						'ns0:customized_field'{
							'datatype:field_code' 'PO_DETAIL_GF_06'
							'datatype:field_value' notEmpty(currentTransaction.PO1_Loop.CTP.findAll{it.CTP02.text()=='NET' && notEmpty(it.CTP11.text())}) ? currentTransaction.PO1_Loop.CTP.findAll{it.CTP02.text()=='NET'}[0].CTP11.text() : null
						}
						'ns0:customized_field'{
							'datatype:field_code' 'PO_DETAIL_GF_07'
							'datatype:field_value' notEmpty(currentTransaction.PO1_Loop.CTP.findAll{it.CTP02.text()=='UCP' && notEmpty(it.CTP08.text())}) ? currentTransaction.PO1_Loop.CTP.findAll{it.CTP02.text()=='UCP'}[0].CTP08.text() : null
						}
						'ns0:customized_field'{
							'datatype:field_code' 'PO_DETAIL_GF_08'
							'datatype:field_value' notEmpty(currentTransaction.PO1_Loop.CTP.findAll{it.CTP02.text()=='ALT' && notEmpty(it.CTP08.text())}) ? currentTransaction.PO1_Loop.CTP.findAll{it.CTP02.text()=='ALT'}[0].CTP08.text() : null
						}
						currentTransaction.TD5.each{currentTD5 ->
							if(currentTD5.TransTypeCode.text() == '38'){
								'ns0:loc_obj'{
									'datatype:LOC_TYPE' 'COUNTRY_OF_ORIGIN'
									if(notEmpty(currentTD5.Routing.text())){
										'datatype:LOC_NAME' currentTD5.Routing.text()
									}
								}
							}
						}
					}
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_24'
					def REF_SA = currentTransaction.REF.findAll{it.ReferenceIdenQualifier.text()=='SA' && notEmpty(it.REF03.text())}
					'datatype:field_value' notEmpty(REF_SA) ? REF_SA[0].REF03.text() : null
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_22'
					def NotifyPartyNode = currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='N1' && notEmpty(it.N1.Name.text())}
					'datatype:field_value' notEmpty(NotifyPartyNode) ? NotifyPartyNode[0].N1.Name.text() : null
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_23'
					'datatype:field_value' ((notEmpty(currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='N1' && notEmpty(it.N3.Name.text())}) ? currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='N1' && notEmpty(it.N3.Name.text())}[0].N3.Name.text() : '') +
					(notEmpty(currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='N1' && notEmpty(it.N3.Name2.text())}) ? ',' + currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='N1' && notEmpty(it.N3.Name2.text())}[0].N3.Name2.text() : '') +
					(notEmpty(currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='N1' && notEmpty(it.N4.N401_CityName.text())}) ? ',' + currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='N1' && notEmpty(it.N4.N401_CityName.text())}[0].N4.N401_CityName.text() : '') +
					(notEmpty(currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='N1' && notEmpty(it.N4.N402_StateProvinceCode.text())}) ? ',' + currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='N1' && notEmpty(it.N4.N402_StateProvinceCode.text())}[0].N4.N402_StateProvinceCode.text() : '') +
					(notEmpty(currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='N1' && notEmpty(it.N4.N403_PostalCode.text())}) ? ',' + currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='N1' && notEmpty(it.N4.N403_PostalCode.text())}[0].N4.N403_PostalCode.text() : '') +
					(notEmpty(currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='N1' && notEmpty(it.N4.N404_CountryCode.text())}) ? ',' + currentTransaction.N1_Loop.findAll{it.N1.IdentityCode.text()=='N1' && notEmpty(it.N4.N404_CountryCode.text())}[0].N4.N404_CountryCode.text() : ''))
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_19'
					DecimalFormat df = new DecimalFormat("#.000");
					'datatype:field_value' ((currentTransaction.TD1.TD108.text() == 'LB') ? df.format((currentTransaction.TD1.TD107.text() as double) * 0.45359237) + ' KG' :  currentTransaction.TD1.TD107.text() + ' ' + currentTransaction.TD1.TD107.text())
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_20'
					'datatype:field_value' currentTransaction.TD1.TD109.text() + ' ' + currentTransaction.TD1.TD110.text()
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_21'
					def Requested_Mode = currentTransaction.TD5.findAll{it.TransTypeCode.text() == 'ND' && notEmpty(it.Routing.text())}
					'datatype:field_value' notEmpty(Requested_Mode) ? Requested_Mode[0].Routing.text() : null
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_14'
					def REF_IT = currentTransaction.REF.findAll{it.ReferenceIdenQualifier.text()=='IT' && notEmpty(it.ReferenceIden.text())}
					'datatype:field_value' notEmpty(REF_IT) ? REF_IT[0].ReferenceIden.text() : null
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_25'
					def Shipment_Type = currentTransaction.TD5.findAll{it.TransTypeCode.text()=='ND' && it.td505.text()}
					'datatype:field_value' notEmpty(Shipment_Type) ? Shipment_Type[0].td505.text() : null
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_13'
					'datatype:field_value' currentTransaction.TD3.TD305.text()
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_09'
					'datatype:field_value' currentTransaction.TD3.TD310.text()
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_18'
					'datatype:field_value' currentTransaction.FOB.FOB03.text()
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_10'
					def REF_VR = currentTransaction.REF.findAll{it.ReferenceIdenQualifier.text()=='VR' && notEmpty(it.ReferenceIden.text())}
					'datatype:field_value' notEmpty(REF_VR) ? REF_VR[0].ReferenceIden.text() : null
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_11'
					def REF_ACB = currentTransaction.REF.findAll{it.ReferenceIdenQualifier.text()=='ACB' && notEmpty(it.ReferenceIden.text())}
					'datatype:field_value' notEmpty(REF_ACB) ? REF_ACB[0].ReferenceIden.text() : null
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_17'
					def REF_LU = currentTransaction.REF.findAll{it.ReferenceIdenQualifier.text()=='LU' && notEmpty(it.ReferenceIden.text())}
					'datatype:field_value' notEmpty(REF_LU) ? REF_LU[0].ReferenceIden.text() : null
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_15'
					def REF_4B = currentTransaction.REF.findAll{it.ReferenceIdenQualifier.text()=='4B' && notEmpty(it.ReferenceIden.text())}
					'datatype:field_value' notEmpty(REF_4B) ? REF_4B[0].ReferenceIden.text() : null
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_16'
					def EDIFND = currentTransaction.TD5.findAll{it.TransTypeCode.text()=='59' && notEmpty(it.td505.text())}
					'datatype:field_value' notEmpty(EDIFND) ? EDIFND[0].td505.text() : null
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_05'
					def REF_GV = currentTransaction.REF.findAll{it.ReferenceIdenQualifier.text()=='GV' && notEmpty(it.ReferenceIden.text())}
					'datatype:field_value' notEmpty(REF_GV) ? REF_GV[0].ReferenceIden.text() : null
				}
				def N9_TOC = currentTransaction.N9_Loop.findAll{it.N9.N901.text() == 'TOC'}
				if(notEmpty(N9_TOC)){
					'ns0:po_remark'{
						'datatype:seq' '1'
						'datatype:remark_code' 'REMARK'
						'datatype:remark_text' currentTransaction.N9_Loop[0].N9.N902.text()
					}
					currentTransaction.N9_Loop.MSG.eachWithIndex{currentMSG,index ->
						'ns0:po_remark'{
							'datatype:seq' index + 2
							'datatype:remark_code' 'REMARK'
							'datatype:remark_text' currentMSG.MSG01.text()
						}
					}
				}
			}
		}
	}

	//mapping logic end
	def outxml = XmlUtil.serialize(writer.toString());

	return outxml
	//def endDt = new Date()
	//println 'cost: '+(endDt.getTime() - currentSystemDt.getTime()) +' ms.'
	//	return writer.toString();
}


/**
 * -- Common mapping function, suggest move to core library to provide such general function like xsl provide function, or tib:xxx in designer.
 */

public String getConversionWithDefault(String TP_ID, String MSG_TYPE_ID, String DIR_ID, String convertTypeId, String fromValue, String defaultValue, Connection conn) throws Exception {
	String ret = getConversion(TP_ID, MSG_TYPE_ID, DIR_ID, convertTypeId, fromValue, conn);
	if (ret==null || ret.length()==0) {
		ret = defaultValue;
	}
	return ret;
}

public String getConversion(String TP_ID, String MSG_TYPE_ID, String DIR_ID, String convertTypeId, String fromValue, Connection conn) throws Exception {
	if (conn==null)
		return "";

	String ret = "";
	PreparedStatement pre = null;
	ResultSet result = null;
	String sql = "select ext_cde from b2b_cde_conversion where tp_id=? and msg_type_id=? and dir_id=? and convert_type_id=? and int_cde=?";
	try {
		pre = conn.prepareStatement(sql);
		pre.setString(1, TP_ID);
		pre.setString(2, MSG_TYPE_ID);
		pre.setString(3, DIR_ID);
		pre.setString(4, convertTypeId);
		pre.setString(5, fromValue);
		result = pre.executeQuery();

		if (result.next()) {
			ret = result.getString(1);
		}
	} finally {
		if (result!=null)
			result.close();
		if (pre!=null)
			pre.close();
	}
	return ret;
}

public String getRuntimeParameter(String name, String[] params) {
	String pn = name+"=";
	for(int i=0; params!=null && i<params.length; i++) {
		String tmp = params[i];
		if (tmp==null || tmp.length()==0)
			continue;
		if (tmp.startsWith(pn)) {
			return tmp.substring(pn.length());
		}
	}
	return "";
}

public String convertDateTime(String inputDate, String inputFormat, String outputFormat) throws Exception {
	String output = "";

	if (inputDate!=null && inputDate.trim().length()>0 && inputFormat!=null && inputFormat.trim().length()>0
	&& outputFormat!=null && outputFormat.trim().length()>0) {

		SimpleDateFormat sfmt = new SimpleDateFormat(inputFormat);
		java.util.Date date = sfmt.parse(inputDate);

		SimpleDateFormat soutfmt = new SimpleDateFormat(outputFormat);
		output = soutfmt.format(date);
	}

	return output;
}

public boolean notEmpty(Object data){
	if(null != data){
		if(data instanceof String){
			return data.length() > 0
		}else if(data instanceof List){
			data.size > 0
		}
	}else{
		return false;
	}
}

public String substring(String str, int start, int length){
	if(null != str && str.length() > 0){
		return str.substring(start,str.length + length)
	}else{
		return ""
	}
}

public def string(def str){
	return str
}