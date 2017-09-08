package b2b.pilot.groovy.scripts

import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat

String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {
	
	//get OLL mapping runtime parameters, "OLLSessionID=aa", "OLL_OriginalSourceFileName=zz", "OLL_SendPortID=yy", "PortProperty=xx"
	def ollSessionId = getRuntimeParameter("OLLSessionID", runtimeParams);
	def sourceFileName = getRuntimeParameter("OLL_OriginalSourceFileName", runtimeParams);
	def portId = getRuntimeParameter("OLL_SendPortID", runtimeParams);
	def portProperty = getRuntimeParameter("PortProperty", runtimeParams);
	//pmt info
	def TP_ID = getRuntimeParameter("TP_ID", runtimeParams);
	def MSG_TYPE_ID = getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
	def DIR_ID = getRuntimeParameter("DIR_ID", runtimeParams);
	def MSG_REQ_ID = getRuntimeParameter("MSG_REQ_ID", runtimeParams);
		
	def writer = new StringWriter()
	def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false))
	//def outXml = new MarkupBuilder(new File("/home/tibco/OLLB2B/LocalTrans/Trash/out.xml").newPrintWriter())
	//def outXml = new StreamingMarkupBuilder()
	
	//mapping script
	
	byte[] bs = inputXmlBody.getBytes();
	def instr = inputXmlBody
	
	//Important: the inputXml is xml root element
	def inXml = new XmlParser().parseText(instr); //XmlSlurper
	
	TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
	def currentSystemDt = new Date()
			
//	println "-------------- session: "+ollSessionId+" start mapping at: "+currentSystemDt.format("yyyy-MM-dd HH:mm:ss.SSS")
	
	
	outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
	
	
	//mapping logic start
	outXml.'ns0:po_file_root' ('xmlns:ns0':'http://www.oocllogistics.com/po', 'xmlns:header':'http://www.oocllogistics.com/header','xmlns:datatype':'http://www.oocllogistics.com/datatype', 'xmlns:notelog':'http://www.oocllogistics.com/notelog', 'xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance')
	{
		
		'ns0:file_header' {
			'header:SENDER_ID'() {'AGEGROUP'}
			'header:RECIPIENT_ID' 'POP'
			'header:MSG_CTRL_REF_NUM' inXml.Header.ISA.ISA13.text()
			'header:MSG_SENT_DATETIME' convertDateTime(inXml.Header.ISA.ISA09.text() + inXml.Header.ISA.ISA10.text(),"yyMMddHHmm","yyyy-MM-dd'T'HH:mm:ss.S")
			'header:MSG_REC_DATETIME' currentSystemDt.format("yyyy-MM-dd") + 'T00:00:00.0'
			'header:MSG_STATUS' 'N'
			'header:MSG_TYPE' 'PO'
			'header:MSG_DEF_VER' '2.01'
		}
		
		//group by item
		inXml.Detail.each { currentDetail ->
//			def vOrderNum = currentOrder.'order-number'.text()
//			def vDepartment = currentOrder.'department'.text()
//			def vSeasonDisplayName = currentOrder.'season-display-name'.text()
//			def currentKey = vOrderNum + "/" + vDepartment + "/" + vSeasonDisplayName
//			
//			if (listPo.contains(currentKey)) {
//				//filter if same , the return here is equals "continue".
//				return
//			} else {
//				listPo.add(currentKey)
//			}
//			
//			//println "order: "+currentKey
//			
			'ns0:po_msg_root' {
				'ns0:msg_header' {
					'header:SENDER_ID' 'AGEGROUP'
					'header:RECIPIENT_ID' 'POP'
					'header:MSG_CTRL_REF_NUM' currentSystemDt.format("yyyyMMdd")
					'header:MSG_SENT_DATETIME' currentSystemDt.format("yyyy-MM-dd") + 'T00:00:00.0'
					'header:MSG_STATUS' getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'AgeGroupBEG01-OLLXMLMsgStatus', currentDetail.BEG.BEG01.text(), 'NEW', conn)
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
							if(notEmpty(currentNameAndAddress1.N1.N104.text())){
								'datatype:PARTNER_EXT_REF_NUM' currentNameAndAddress1.N1.N104.text()
							}
							'datatype:ADDR' {
								if(notEmpty(currentNameAndAddress1.N4.N104.text())){
									'datatype:country_name' currentNameAndAddress1.N4.N104.text()
								}
								if(notEmpty(currentNameAndAddress1.N4.N402.text())){
									'datatype:state_name' currentNameAndAddress1.N4.N402.text()
								}
								'datatype:city'{
									if(notEmpty(currentNameAndAddress1.N4.N401.text())){
										'datatype:LOC_NAME' currentNameAndAddress1.N4.N401.text()
									}
								}
								if(notEmpty(currentNameAndAddress1.N4.N403.text())){
									'datatype:postal_code' currentNameAndAddress1.N4.N403.text()
								}
								'datatype:ADDR_LINE' currentNameAndAddress1.N3.N301.text()
								if(notEmpty(currentNameAndAddress1.N3.N302.text())){
									'datatype:ADDR_LINE' currentNameAndAddress1.N3.N302.text()
								}
							}
						}
					}
				}
				currentDetail.NameAndAddressLoop.each{ currentNameAndAddress2 ->
					if('BS' == currentNameAndAddress2.N1.N101.text()){
						'ns0:csgn' {
							'datatype:PARTNER_NAME' currentNameAndAddress2.N1.N102.text()
							if(notEmpty(currentNameAndAddress2.N1.N104.text())){
								'datatype:PARTNER_EXT_REF_NUM' currentNameAndAddress2.N1.N104.text()
							}
							'datatype:ADDR' {
								if(notEmpty(currentNameAndAddress2.N4.N404.text())){
									'datatype:country_code' currentNameAndAddress2.N4.N404.text()
								}
								if(notEmpty(currentNameAndAddress2.N4.N404.text())){
									'datatype:country_name' currentNameAndAddress2.N4.N404.text()
								}
								if(notEmpty(currentNameAndAddress2.N4.N402.text())){
									'datatype:state_code' currentNameAndAddress2.N4.N402.text()
								}
								if(notEmpty(currentNameAndAddress2.N4.N402.text())){
									'datatype:state_name' currentNameAndAddress2.N4.N402.text()
								}
								'datatype:city'{
									if(notEmpty(currentNameAndAddress2.N4.N401.text())){
										'datatype:LOC_NAME' currentNameAndAddress2.N4.N401.text()
									}
								}
								if(notEmpty(currentNameAndAddress2.N4.N403.text())){
									'datatype:postal_code' currentNameAndAddress2.N4.N403.text()
								}
								'datatype:ADDR_LINE' currentNameAndAddress2.N3.N301.text()
								if(notEmpty(currentNameAndAddress2.N3.N302.text())){
									'datatype:ADDR_LINE' currentNameAndAddress2.N3.N302.text()
								}
							}
							currentDetail.REF.each{ currentRef ->
								if('DP' == currentRef.REF01.text() && notEmpty(currentRef.REF02.text())){
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
							if(notEmpty(currentNameAndAddress3.N1.N104.text())){
								'datatype:PARTNER_EXT_REF_NUM' currentNameAndAddress3.N1.N104.text()
							}
							'datatype:ADDR' {
								if(notEmpty(currentNameAndAddress3.N4.N404.text())){
									'datatype:country_code' currentNameAndAddress3.N4.N404.text()
								}
								if(notEmpty(currentNameAndAddress3.N4.N404.text())){
									'datatype:country_name' currentNameAndAddress3.N4.N404.text()
								}
								if(notEmpty(currentNameAndAddress3.N4.N402.text())){
									'datatype:state_name' currentNameAndAddress3.N4.N402.text()
								}
								'datatype:city'{
									if(notEmpty(currentNameAndAddress3.N4.N401.text())){
										'datatype:LOC_NAME' currentNameAndAddress3.N4.N401.text()
									}
								}
								if(notEmpty(currentNameAndAddress3.N4.N403.text())){
									'datatype:postal_code' currentNameAndAddress3.N4.N403.text()
								}
								'datatype:ADDR_LINE' currentNameAndAddress3.N3.N301.text()
								if(notEmpty(currentNameAndAddress3.N3.N302.text())){
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
							if(notEmpty(currentNameAndAddress4.N1.N104.text())){
								'datatype:PARTNER_EXT_REF_NUM' currentNameAndAddress4.N1.N104.text()
							}
						}
					}
				}
				'ns0:po_attr' {
					'datatype:data_status_code' getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'AgeGroupBEG01-OLLDataStatus', currentDetail.BEG.BEG01.text(), 'STATUS_ACTIVE', conn)
					'datatype:crt_date' {
						'datatype:record_datetime' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
					}
					'datatype:issue_date' {
						'datatype:record_datetime'  convertDateTime(currentDetail.BEG.BEG05.text(),"yyyyMMdd","yyyy-MM-dd'T'HH:mm:ss.S")
					}
				}
				'ns0:payment_method'{
					'datatype:payment_method_code'	"CC" == currentDetail.FOB.FOB01.text() ? 'C' : "PP" == currentDetail.FOB.FOB01.text() ? 'P' : ''
					'datatype:ext_ref' currentDetail.FOB.FOB01.text()
				}
				'ns0:ship_via'{
					'datatype:ship_via_code' "A" == currentDetail.TD5.TD504.text() ? "A" : "SE" == currentDetail.TD5.TD504.text() ? "B" : "S"
					if(notEmpty(currentDetail.TD5.TD504.text())){
						'datatype:ship_via_ext_ref' currentDetail.TD5.TD504.text()
					}
				}
				
				currentDetail.REF.each{currentRef ->
					if('4B' == currentRef.REF01.text()){
						'ns0:por'{
							if(notEmpty(currentRef.REF02.text())){
								'datatype:LOC_NAME' currentRef.REF02.text()
							}
							if(notEmpty(currentDetail.FOB.FOB07.text())){
								'datatype:LOC_NAME' currentDetail.FOB.FOB07.text()
							}
							if(notEmpty(currentRef.REF02.text())){
								'datatype:cust_ref' currentRef.REF02.text()
							}
							if(notEmpty(currentDetail.FOB.FOB07.text())){
								'datatype:cust_ref' currentDetail.FOB.FOB07.text()
							}
						}
					}
				}
				currentDetail.NameAndAddressLoop.each{ currentNameAndAddress5 ->
					if('BS' == currentNameAndAddress5.N1.N101.text()){
						'ns0:fnd'{
							if(notEmpty(currentNameAndAddress5.N4.N401.text())){
								'datatype:LOC_NAME' currentNameAndAddress5.N4.N401.text()
							}
							if(notEmpty(currentNameAndAddress5.N4.N401.text())){
								'datatype:cust_ref' currentNameAndAddress5.N4.N401.text()
							}
						}
					}
				}
				currentDetail.PO1Loop.each{ currentPO ->
					'ns0:po_item'{
						'datatype:sku'{
							if(currentPO.PO1.PO107.text()){
								'datatype:STYLE_CODE' currentPO.PO1.PO107.text()
							}
							if(currentPO.PO1.PO117.text()){
								'datatype:COLOR_CODE' currentPO.PO1.PO117.text()
							}
							if(currentPO.PO1.PO119.text()){
								'datatype:SIZE_CODE' currentPO.PO1.PO119.text()
							}
							if(currentPO.PO1.PO117.text()){
								'datatype:DESC' currentPO.PO1.PO109.text()
							}
						}
						'datatype:lowest_uom'{
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
								if('037' == currentDTM1.DTM01.text()){
									'datatype:earliest_pol_etd_date' {
										'datatype:record_datetime' convertDateTime(currentDTM1.DTM02.text(),'yyyyMMdd',"yyyy-MM-dd")+'T00:00:00.0'
									}
								}
								if('037' == currentDTM1.DTM01.text()){
									'datatype:target_pol_etd_date' {
										'datatype:record_datetime' convertDateTime(currentDTM1.DTM02.text(),'yyyyMMdd',"yyyy-MM-dd")+'T00:00:00.0'
									}
								}
								if('038' == currentDTM1.DTM01.text()){
									'datatype:latest_pol_etd_date' {
										'datatype:record_datetime' convertDateTime(currentDTM1.DTM02.text(),'yyyyMMdd',"yyyy-MM-dd")+'T00:00:00.0'
									}
								}
								if('996' == currentDTM1.DTM01.text()){
									'datatype:fnd_eta_date' {
										'datatype:record_datetime' convertDateTime(currentDTM1.DTM02.text(),'yyyyMMdd',"yyyy-MM-dd")+'T00:00:00.0'
									}
								}
							}
						}
						'ns0:origin'{
							if('CC' == currentDetail.FOB.FOB01.text()){
								'datatype:LOC_NAME' currentDetail.FOB.FOB07.text()
								'datatype:cust_ref' currentDetail.FOB.FOB07.text()
							}
						}
						'ns0:ship_via'{
							'datatype:ship_via_code' getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'AGEGROUPShipVia-OLLShipVia', currentPO.TD5.TD504.text(), currentPO.TD5.TD504.text(), conn)
							if(notEmpty(currentPO.TD5.TD504.text())){
								'datatype:ship_via_ext_ref' currentPO.TD5.TD504.text()
							}
						}
						'ns0:lowest_uom_cost'{
							'datatype:cur_code' ('xsi:nil':'true')
							if(notEmpty(currentPO.PO1.PO104.text())){
								'datatype:amt' currentPO.PO1.PO104.text()
							}
						}
						if(notEmpty(currentPO.PO1.PO111.text())){
							'ns0:hts_ref' currentPO.PO1.PO111.text()
						}
						if(notEmpty(currentPO.SLN.SLN10.text())){
							'ns0:upc_ref' currentPO.SLN.SLN10.text()
						}
						if(notEmpty(currentPO.PO1.PO101.text())){
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
							if('VN' == currentNameAndAddress6.N1.N101.text()){
								if(notEmpty(currentNameAndAddress6.N4.N404.text())){
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
				}
				'ns0:customized_field'{ 
					'datatype:field_code' 'PO_HEADER_GF_01'
					'datatype:field_value' currentDetail.BEG.BEG03.text()
				}
				
			}
		}
	}
	//mapping logic end
	
	def outxml = XmlUtil.serialize(writer.toString());
//	println outxml;
	
//	println "finished: "+currentSystemDt.format("yyyy-MM-dd HH:mm:ss.SSS")
	
	return outxml;
	
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

boolean notEmpty(Object data){
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

