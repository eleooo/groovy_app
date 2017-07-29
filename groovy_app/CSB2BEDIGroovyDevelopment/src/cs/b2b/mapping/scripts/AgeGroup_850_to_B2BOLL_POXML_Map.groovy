package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat


String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();

	/**
	 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
	 */
	//no here

	/**
	 * Part II: get mapping runtime parameters
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
	def AgeGroup_PO_MSG_Root = new XmlParser().parseText(inputXmlBody);

	def writer = new StringWriter()
	def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
	outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")


	/**
	 * Part IV: mapping script start from here
	 */

	TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
	def currentSystemDt = new Date()

	outXml.'ns0:po_file_root' ('xmlns:ns0':'http://www.oocllogistics.com/po', 'xmlns:header':'http://www.oocllogistics.com/header',
	'xmlns:datatype':'http://www.oocllogistics.com/datatype', 'xmlns:notelog':'http://www.oocllogistics.com/notelog', 'xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance')
	{

		'ns0:file_header' {
			'header:SENDER_ID' 'AGEGROUP'
			'header:RECIPIENT_ID' 'POP'
			'header:MSG_CTRL_REF_NUM' AgeGroup_PO_MSG_Root.Header.ISA.ISA13.text()
			'header:MSG_SENT_DATETIME' util.convertDateTime(AgeGroup_PO_MSG_Root.Header.ISA.ISA09.text() + AgeGroup_PO_MSG_Root.Header.ISA.ISA10.text(), "yyMMddHHmm", "yyyy-MM-dd'T'HH:mm:ss.S")
			'header:MSG_REC_DATETIME' currentSystemDt.format("yyyy-MM-dd") + 'T00:00:00.0'
			'header:MSG_STATUS' 'N'
			'header:MSG_TYPE' 'PO'
			'header:MSG_DEF_VER' '2.01'
		}

		//group by item
		AgeGroup_PO_MSG_Root.Detail.each { currentDetail ->
			'ns0:po_msg_root' {
				'ns0:msg_header' {
					'header:SENDER_ID' 'AGEGROUP'
					'header:RECIPIENT_ID' 'POP'
					'header:MSG_CTRL_REF_NUM' currentSystemDt.format("yyyyMMdd")
					'header:MSG_SENT_DATETIME' currentSystemDt.format("yyyy-MM-dd") + 'T00:00:00.0'
					'header:MSG_STATUS' util.getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'AgeGroupBEG01-OLLXMLMsgStatus', currentDetail.BEG.BEG01.text(), 'NEW', conn)
					'header:MSG_TYPE' 'PO'
					'header:MSG_DEF_VER' '2.01'
					'header:data_header' {
						'header:to' 'POP'
						'header:from' 'AGEGROUP'
						'header:time_stamp' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
						'header:header_msg_type' 'PO'
						'header:body_msg_type' 'PO'
						'header:msg_ver' '2.01'
					}
				}

				'ns0:ext_po_num' currentDetail.BEG.BEG03.text()
				'ns0:po_num' currentDetail.BEG.BEG03.text()

				currentDetail.NameAndAddressLoop.each{ currentNameAndAddress1 ->
					if('BS' == currentNameAndAddress1.N1.N101.text()){
						'ns0:cust' {
							'datatype:PARTNER_NAME' currentNameAndAddress1.N1.N102.text()
							if(util.notEmpty(currentNameAndAddress1.N1.N104.text())) {
								'datatype:PARTNER_EXT_REF_NUM' currentNameAndAddress1.N1.N104.text()
							}
							'datatype:ADDR' {
								if(util.notEmpty(currentNameAndAddress1.N4.N104.text())) {
									'datatype:country_name' currentNameAndAddress1.N4.N104.text()
								}
								if(util.notEmpty(currentNameAndAddress1.N4.N402.text())) {
									'datatype:state_name' currentNameAndAddress1.N4.N402.text()
								}
								'datatype:city' {
									if(util.notEmpty(currentNameAndAddress1.N4.N401.text())) {
										'datatype:LOC_NAME' currentNameAndAddress1.N4.N401.text()
									}
								}
								if(util.notEmpty(currentNameAndAddress1.N4.N403.text())) {
									'datatype:postal_code' currentNameAndAddress1.N4.N403.text()
								}
								'datatype:ADDR_LINE' currentNameAndAddress1.N3.N301.text()
								if(util.notEmpty(currentNameAndAddress1.N3.N302.text())) {
									'datatype:ADDR_LINE' currentNameAndAddress1.N3.N302.text()
								}
							}
						}
					}
				}
				currentDetail.NameAndAddressLoop.each{ currentNameAndAddress2 ->
					if('BS' == currentNameAndAddress2.N1.N101.text()) {
						'ns0:csgn' {
							'datatype:PARTNER_NAME' currentNameAndAddress2.N1.N102.text()
							if(util.notEmpty(currentNameAndAddress2.N1.N104.text())) {
								'datatype:PARTNER_EXT_REF_NUM' currentNameAndAddress2.N1.N104.text()
							}
							'datatype:ADDR' {
								if(util.notEmpty(currentNameAndAddress2.N4.N404.text())) {
									'datatype:country_code' currentNameAndAddress2.N4.N404.text()
								}
								if(util.notEmpty(currentNameAndAddress2.N4.N404.text())) {
									'datatype:country_name' currentNameAndAddress2.N4.N404.text()
								}
								if(util.notEmpty(currentNameAndAddress2.N4.N402.text())) {
									'datatype:state_code' currentNameAndAddress2.N4.N402.text()
								}
								if(util.notEmpty(currentNameAndAddress2.N4.N402.text())) {
									'datatype:state_name' currentNameAndAddress2.N4.N402.text()
								}
								'datatype:city' {
									if(util.notEmpty(currentNameAndAddress2.N4.N401.text())) {
										'datatype:LOC_NAME' currentNameAndAddress2.N4.N401.text()
									}
								}
								if(util.notEmpty(currentNameAndAddress2.N4.N403.text())) {
									'datatype:postal_code' currentNameAndAddress2.N4.N403.text()
								}
								'datatype:ADDR_LINE' currentNameAndAddress2.N3.N301.text()
								if(util.notEmpty(currentNameAndAddress2.N3.N302.text())) {
									'datatype:ADDR_LINE' currentNameAndAddress2.N3.N302.text()
								}
							}
							currentDetail.REF.each{ currentRef ->
								if('DP' == currentRef.REF01.text() && util.notEmpty(currentRef.REF02.text())) {
									'ns0:dept' currentRef.REF02.text()
								}
							}
						}
					}
				}
				currentDetail.NameAndAddressLoop.each{ currentNameAndAddress3 ->
					if('VN' == currentNameAndAddress3.N1.N101.text()){
						'ns0:vdr' {
							'datatype:PARTNER_NAME' currentNameAndAddress3.N1.N102.text()
							if(util.notEmpty(currentNameAndAddress3.N1.N104.text())) {
								'datatype:PARTNER_EXT_REF_NUM' currentNameAndAddress3.N1.N104.text()
							}
							'datatype:ADDR' {
								if(util.notEmpty(currentNameAndAddress3.N4.N404.text())) {
									'datatype:country_code' currentNameAndAddress3.N4.N404.text()
								}
								if(util.notEmpty(currentNameAndAddress3.N4.N404.text())) {
									'datatype:country_name' currentNameAndAddress3.N4.N404.text()
								}
								if(util.notEmpty(currentNameAndAddress3.N4.N402.text())) {
									'datatype:state_name' currentNameAndAddress3.N4.N402.text()
								}
								'datatype:city'{
									if(util.notEmpty(currentNameAndAddress3.N4.N401.text())) {
										'datatype:LOC_NAME' currentNameAndAddress3.N4.N401.text()
									}
								}
								if(util.notEmpty(currentNameAndAddress3.N4.N403.text())) {
									'datatype:postal_code' currentNameAndAddress3.N4.N403.text()
								}
								'datatype:ADDR_LINE' currentNameAndAddress3.N3.N301.text()
								if(util.notEmpty(currentNameAndAddress3.N3.N302.text())) {
									'datatype:ADDR_LINE' currentNameAndAddress3.N3.N302.text()
								}
							}
						}
					}
				}
				currentDetail.NameAndAddressLoop.each{ currentNameAndAddress4 ->
					if('BT' == currentNameAndAddress4.N1.N101.text()){
						'ns0:billto' {
							'datatype:PARTNER_NAME' currentNameAndAddress4.N1.N102.text()
							if(util.notEmpty(currentNameAndAddress4.N1.N104.text())){
								'datatype:PARTNER_EXT_REF_NUM' currentNameAndAddress4.N1.N104.text()
							}
						}
					}
				}
				'ns0:po_attr' {
					'datatype:data_status_code' util.getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'AgeGroupBEG01-OLLDataStatus', currentDetail.BEG.BEG01.text(), 'STATUS_ACTIVE', conn)
					'datatype:crt_date' {
						'datatype:record_datetime' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
					}
					'datatype:issue_date' {
						'datatype:record_datetime'  util.convertDateTime(currentDetail.BEG.BEG05.text(),"yyyyMMdd","yyyy-MM-dd'T'HH:mm:ss.S")
					}
				}
				'ns0:payment_method' {
					'datatype:payment_method_code'	("CC"==currentDetail.FOB.FOB01.text())?'C':("PP"==currentDetail.FOB.FOB01.text()?'P':'')
					'datatype:ext_ref' currentDetail.FOB.FOB01.text()
				}
				'ns0:ship_via' {
					'datatype:ship_via_code' ("A" == currentDetail.TD5.TD504.text())?"A":("SE"==currentDetail.TD5.TD504.text()?"B":"S")
					if(util.notEmpty(currentDetail.TD5.TD504.text())) {
						'datatype:ship_via_ext_ref' currentDetail.TD5.TD504.text()
					}
				}

				currentDetail.REF.each{currentRef ->
					if('4B' == currentRef.REF01.text()){
						'ns0:por'{
							if(util.notEmpty(currentRef.REF02.text())){
								'datatype:LOC_NAME' currentRef.REF02.text()
							}
							if(util.notEmpty(currentDetail.FOB.FOB07.text())){
								'datatype:LOC_NAME' currentDetail.FOB.FOB07.text()
							}
							if(util.notEmpty(currentRef.REF02.text())){
								'datatype:cust_ref' currentRef.REF02.text()
							}
							if(util.notEmpty(currentDetail.FOB.FOB07.text())){
								'datatype:cust_ref' currentDetail.FOB.FOB07.text()
							}
						}
					}
				}
				currentDetail.NameAndAddressLoop.each{ currentNameAndAddress5 ->
					if('BS' == currentNameAndAddress5.N1.N101.text()){
						'ns0:fnd'{
							if(util.notEmpty(currentNameAndAddress5.N4.N401.text())){
								'datatype:LOC_NAME' currentNameAndAddress5.N4.N401.text()
							}
							if(util.notEmpty(currentNameAndAddress5.N4.N401.text())){
								'datatype:cust_ref' currentNameAndAddress5.N4.N401.text()
							}
						}
					}
				}
				currentDetail.PO1Loop.each{ currentPO ->
					'ns0:po_item'{
						'datatype:sku'{
							if(currentPO.PO1.PO107.text()) {
								'datatype:STYLE_CODE' currentPO.PO1.PO107.text()
							}
							if(currentPO.PO1.PO117.text()) {
								'datatype:COLOR_CODE' currentPO.PO1.PO117.text()
							}
							if(currentPO.PO1.PO119.text()) {
								'datatype:SIZE_CODE' currentPO.PO1.PO119.text()
							}
							if(currentPO.PO1.PO117.text()) {
								'datatype:DESC' currentPO.PO1.PO109.text()
							}
						}
						'datatype:lowest_uom' {
							if(currentPO.PO1.PO103.text()){
								'datatype:UOM_CODE' currentPO.PO1.PO103.text()
							}
							if(currentPO.PO1.PO103.text()){
								'datatype:uom_ext_ref' currentPO.PO1.PO103.text()
							}
						}
						if(currentPO.PO1.PO102.text()){
							'datatype:lowest_uom_qty' currentPO.PO1.PO102.text()
						}
						'ns0:ship_windows'{
							currentPO.DTM.each{currentDTM1->
								if('037' == currentDTM1.DTM01.text()) {
									'datatype:earliest_pol_etd_date' {
										'datatype:record_datetime' util.convertDateTime(currentDTM1.DTM02.text(),'yyyyMMdd',"yyyy-MM-dd")+'T00:00:00.0'
									}
								}
								if('037' == currentDTM1.DTM01.text()){
									'datatype:target_pol_etd_date' {
										'datatype:record_datetime' util.convertDateTime(currentDTM1.DTM02.text(),'yyyyMMdd',"yyyy-MM-dd")+'T00:00:00.0'
									}
								}
								if('038' == currentDTM1.DTM01.text()){
									'datatype:latest_pol_etd_date' {
										'datatype:record_datetime' util.convertDateTime(currentDTM1.DTM02.text(),'yyyyMMdd',"yyyy-MM-dd")+'T00:00:00.0'
									}
								}
								if('996' == currentDTM1.DTM01.text()){
									'datatype:fnd_eta_date' {
										'datatype:record_datetime' util.convertDateTime(currentDTM1.DTM02.text(),'yyyyMMdd',"yyyy-MM-dd")+'T00:00:00.0'
									}
								}
							}
						}
						'ns0:origin' {
							if('CC' == currentDetail.FOB.FOB01.text()) {
								'datatype:LOC_NAME' currentDetail.FOB.FOB07.text()
								'datatype:cust_ref' currentDetail.FOB.FOB07.text()
							}
						}
						'ns0:ship_via' {
							'datatype:ship_via_code' util.getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'AGEGROUPShipVia-OLLShipVia', currentPO.TD5.TD504.text(), currentPO.TD5.TD504.text(), conn)
							if (util.notEmpty(currentPO.TD5.TD504.text())) {
								'datatype:ship_via_ext_ref' currentPO.TD5.TD504.text()
							}
						}
						'ns0:lowest_uom_cost' {
							'datatype:cur_code' ('xsi:nil':'true')
							if (util.notEmpty(currentPO.PO1.PO104.text())) {
								'datatype:amt' currentPO.PO1.PO104.text()
							}
						}
						if(util.notEmpty(currentPO.PO1.PO111.text())) {
							'ns0:hts_ref' currentPO.PO1.PO111.text()
						}
						if(util.notEmpty(currentPO.SLN.SLN10.text())){
							'ns0:upc_ref' currentPO.SLN.SLN10.text()
						}
						if(util.notEmpty(currentPO.PO1.PO101.text())){
							'ns0:event_ref' currentPO.PO1.PO101.text()
						}
						'ns0:customized_field' {
							'datatype:field_code' 'PO_DETAIL_GF_02'
							'datatype:field_value' currentPO.PO1.PO115.text()
						}
						'ns0:customized_field' {
							'datatype:field_code' 'PO_DETAIL_GF_04'
							'datatype:field_value' currentPO.PO1.PO121.text()
						}
						'ns0:customized_field' {
							'datatype:field_code' 'PO_DETAIL_GF_03'
							'datatype:field_value' currentPO.PO1.PO101.text()
						}
						currentDetail.NameAndAddressLoop.each{ currentNameAndAddress6 ->
							if('VN' == currentNameAndAddress6.N1.N101.text()) {
								if(util.notEmpty(currentNameAndAddress6.N4.N404.text()))
									'ns0:loc_obj'{
										'datatype:LOC_TYPE' 'COUNTRY_OF_ORIGIN'
										'datatype:LOC_NAME' currentNameAndAddress6.N4.N404.text()
										'datatype:COUNTRY_NAME' currentNameAndAddress6.N4.N404.text()
										'datatype:cust_ref' currentNameAndAddress6.N4.N404.text()
									}
								return
							}
						}
					}
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_01'
					'datatype:field_value' currentDetail.BEG.BEG03.text()
				}

			}
		}
	}

	return writer.toString();
}

