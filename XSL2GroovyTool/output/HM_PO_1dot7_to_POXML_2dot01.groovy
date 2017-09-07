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
	if (-17 == bs[0] && -69 == bs[1] && -65 == bs[2]) {
		instr = new String(bs, 3, bs.length-3);
	} else if (-17 == bs[0] && -65 == bs[1] && -67 == bs[2]) {
		instr = new String(bs, 3, bs.length-3);
	} else if (-17 == bs[0] && -69 == bs[1] && 63 == bs[2]) {
		instr = new String(bs, 3, bs.length-3);
	} else if (63 == bs[0] && 63 == bs[1] && 63 == bs[2]) {
		instr = new String(bs, 3, bs.length-3);
	}

	//Important: the inputXml is xml root element
	def order_message = new XmlParser().parseText(instr); //XmlSlurper

	TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
	def currentSystemDt = new Date()

	println "-------------- session: "+ollSessionId+" start mapping at: "+currentSystemDt.format("yyyy-MM-dd HH:mm:ss.SSS")


	outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
	//mapping logic start
	outXml.'ns0:po_file_root' ('xmlns:ns0':'http://www.oocllogistics.com/po', 'xmlns:header':'http://www.oocllogistics.com/header',
	'xmlns:datatype':'http://www.oocllogistics.com/datatype', 'xmlns:sp':'http://www.oocllogistics.com/sp', 'xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance')
	{
		def vMsgId = order_message.'message-id'.text()
		vMsgId = vMsgId.substring(vMsgId.length()-9)

		'ns0:file_header' {
			'header:SENDER_ID' 'HM'
			'header:RECIPIENT_ID' 'POP'
			'header:MSG_CTRL_REF_NUM' vMsgId
			'header:MSG_SENT_DATETIME' order_message.'message-creation-time'.text()
			'header:MSG_REC_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
			'header:MSG_STATUS' 'N'
			'header:MSG_TYPE' 'PO'
			'header:MSG_DEF_VER' '2.01'
		}

		//group by item
		def listPo = []  //a string array to keep which po_num already process, use for grouping
		order_message.order.each { currentOrder ->
			def vOrderNum = currentOrder.'order-number'.text()
			def vDepartment = currentOrder.'department'.text()
			def vSeasonDisplayName = currentOrder.'season-display-name'.text()
			def currentKey = vOrderNum + "/" + vDepartment + "/" + vSeasonDisplayName

			if (listPo.contains(currentKey)) {
				//filter if same , the return here is equals "continue".
				return
			} else {
				listPo.add(currentKey)
			}

			//println "order: "+currentKey

			'ns0:po_msg_root' {
				'ns0:msg_header' {
					'header:SENDER_ID' 'HM'
					'header:RECIPIENT_ID' 'POP'
					'header:MSG_CTRL_REF_NUM' '##H_AND_M_PO_CTRL_NUM##'
					'header:MSG_SENT_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
					'header:MSG_STATUS' 'N'
					'header:MSG_TYPE' 'PO'
					'header:MSG_DEF_VER' '2.01'
					'header:data_header' {
						'header:to' 'POP'
						'header:from' 'HM'
						'header:time_stamp' (currentOrder.'order-issuing-date'.text() + "T00:00:00")
						'header:header_msg_type' 'PO'
						'header:body_msg_type' 'PO'
						'header:msg_ver' '2.01'
					}
				}
				'ns0:ext_po_num' currentOrder.'order-number'.text()
				'ns0:po_num' currentOrder.'order-number'.text() + '/' + currentOrder.'department'.text() + '/' + currentOrder.'season-display-name'.text()
				'ns0:cust' {}
				'ns0:csgn' {
					'ns0:div' currentOrder.'department'.text()
					'ns0:dept' currentOrder.'corporate-brand-id'.text()
				}
				//mfr
				'ns0:mfr' {
					'datatype:PARTNER_NAME' currentOrder.'production-unit'.'name'.text()
					'datatype:PARTNER_EXT_REF_NUM' currentOrder.'production-unit'.'id'.text()
				}
				'ns0:vdr' {
					'datatype:PARTNER_NAME' currentOrder.'supplier'.'name'.text()
					'datatype:PARTNER_EXT_REF_NUM' currentOrder.'supplier'.'id'.text()
				}
				'ns0:po_attr' {
					'datatype:data_status_code' 'STATUS_ACTIVE'
					'datatype:crt_date' {
						'datatype:record_datetime' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
					}
					'datatype:issue_date' {
						'datatype:record_datetime'  (currentOrder.'order-issuing-date'.text() + 'T00:00:00')
					}
				}
				'ns0:cust_event_ref' currentOrder.'corporate-brand-name'.text()
				'ns0:string_obj' {
					'datatype:type_code' 'HAZ_MAT_CLASS'
					'datatype:string_value' currentOrder.'product'.'hazardous-materials'.'class'.text()
				}
				'ns0:string_obj' {
					'datatype:type_code' 'HAZ_MAT_DESC'
					'datatype:string_value' currentOrder.'product'.'hazardous-materials'.'dangerous-goods-description'.text()
				}
				'ns0:string_obj' {
					'datatype:type_code' 'ESN_1'
					'datatype:string_value' currentOrder.'product'.'hazardous-materials'.'emergency-schedule-number-1'.text()
				}
				'ns0:string_obj' {
					'datatype:type_code' 'ESN_2'
					'datatype:string_value' currentOrder.'product'.'hazardous-materials'.'emergency-schedule-number-2'.text()
				}
				'ns0:string_obj' {
					'datatype:type_code' 'UNIT_FLASH_POINT'
					'datatype:string_value' currentOrder.'product'.'hazardous-materials'.'temperature-unit'.text()
				}
				'ns0:string_obj' {
					'datatype:type_code' 'MEA_METHOD'
					'datatype:string_value' currentOrder.'product'.'hazardous-materials'.'measurement-method'.text()
				}
				'ns0:string_obj' {
					'datatype:type_code' 'PACKING_GROUP'
					'datatype:string_value' currentOrder.'product'.'hazardous-materials'.'packing-group'.text()
				}
				'ns0:string_obj' {
					'datatype:type_code' 'HAZ_MAT_CODE'
					'datatype:string_value' currentOrder.'product'.'hazardous-materials'.'un-code'.text()
				}

				//loop for group of order, get all order-line
				//order_message.order.findAll({it.'order-number'.text() == vOrderNum && it.'department'.text() == vDepartment && it.'season-display-name'.text() == vSeasonDisplayName}).'order-line'.each { currentOrderLine ->

				//def orderLineGroup = order_message.order.findAll{it.'order-number'.text() == vOrderNum && it.'department'.text() == vDepartment && it.'season-display-name'.text() == vSeasonDisplayName}.'order-line'
				//orderLineGroup.each{ currentOrderLine ->


				//				order_message.order.each { curOrd ->
				//					def vCheckOrderNum = curOrd.'order-number'.text()
				//					def vCheckDepartment = curOrd.'department'.text()
				//					def vCheckSeasonDisplayName = curOrd.'season-display-name'.text()
				//
				//					if (vCheckOrderNum!=vOrderNum || vCheckDepartment!=vDepartment || vCheckSeasonDisplayName!=vSeasonDisplayName) {
				//						return
				//					}
				//					curOrd.'order-line'.eachWithIndex { currentOrderLine, idx ->

				//	println "    >> lines: "+ idx



				def allOrders = order_message.order.findAll{it.'order-number'.text() == vOrderNum && it.'department'.text() == vDepartment && it.'season-display-name'.text() == vSeasonDisplayName}
				//def allOrders = order_message.order.grep{it.'order-number'.text() == vOrderNum && it.'department'.text() == vDepartment && it.'season-display-name'.text() == vSeasonDisplayName}
				if(notEmpty(allOrders)){
					'test' allOrders[0].aa
				}

				allOrders.each { cord ->
					cord.'order-line'.each { currentOrderLine ->

						'ns0:po_item' {
							'datatype:sku' {
								'datatype:STYLE_CODE' vOrderNum + "/" + vDepartment
								'datatype:COLOR_CODE' currentOrderLine.'place-of-loading'.text()
								'datatype:SIZE_CODE' currentOrderLine.'hm-reception-date'.text()
							}
							'datatype:sku_measurement' {
								def vVol = currentOrderLine.'volume'.text()
								if (vVol==null || vVol.trim().length()==0) {
									vVol = '0';
								}
								def vWgt = currentOrderLine.'weight'.text()
								if (vWgt==null || vWgt.trim().length()==0) {
									vWgt = '0';
								}
								'datatype:vol' vVol
								'datatype:wgt' vWgt
							}
							def vPartialDelItemQty = currentOrderLine.'partial-delivery-item-quantity'.text()
							if (vPartialDelItemQty!=null && vPartialDelItemQty.length()>0) {
								'datatype:lowest_uom_qty' vPartialDelItemQty
							} else {
								'datatype:lowest_uom_qty' currentOrderLine.'item-quantity'.text()
							}

							'ns0:ship_windows' {
								def vSupplierDeliverDt = currentOrderLine.'supplier-delivery-date'.text() + 'T00:00:00'
								'datatype:por_etd_date' { 'datatype:record_datetime' vSupplierDeliverDt }
								'datatype:earliest_pol_etd_date' { 'datatype:record_datetime' vSupplierDeliverDt }
								'datatype:target_pol_etd_date' { 'datatype:record_datetime' vSupplierDeliverDt }
								'datatype:fnd_eta_date' {
									'datatype:record_datetime' currentOrderLine.'hm-reception-date'.text() + 'T00:00:00'
								}
							}

							'ns0:vdr_sku' {
								'datatype:STYLE_CODE' currentOrderLine.'planning-market-code'.text()
							}
							'ns0:origin' {
								'datatype:UNLOCODE' currentOrderLine.'place-of-loading'.text()
								'datatype:cust_ref' currentOrderLine.'place-of-loading'.text()
							}
							'ns0:fnd' {
								'datatype:cust_ref' currentOrderLine.'planning-market-code'.text()
							}
							'ns0:ship_via' {
								'datatype:ship_via_code' getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'HMshipviacode-ollshipviacode', currentOrderLine.'mode-of-transport'.text(), 'S', conn)
							}
							def vItemPrice = currentOrderLine.'item-price'.text()
							if (vItemPrice!=null && vItemPrice.length()>0) {
								'ns0:lowest_uom_price' {
									'datatype:cur_code' ''
									'datatype:amt' vItemPrice
								}
							}
							'ns0:category_ref' currentOrderLine.'delivery-number'.text()
							'ns0:cust_whs' {
								'datatype:PARTNER_EXT_REF_NUM' currentOrderLine.'planning-market-abbreviation'.text()
							}
							'ns0:event_ref' currentOrderLine.'..'.'mode-of-packing'.text()

							'ns0:customized_field' {
								'datatype:field_code' 'PO_DETAIL_GF_01'
								'datatype:field_value' currentOrderLine.'market-channel'.text()
							}
							'ns0:customized_field' {
								'datatype:field_code' 'PO_DETAIL_GF_02'
								'datatype:field_value' currentOrderLine.'terms-of-delivery'.text()
							}
							'ns0:customized_field' {
								'datatype:field_code' 'PO_DETAIL_GF_13'
								def vPossibleSplit = currentOrderLine.'possible-split'.text()
								'datatype:field_value' getConversion(TP_ID, MSG_TYPE_ID, DIR_ID, 'HMSplitflag-CustomizedSplitflag', vPossibleSplit, conn)
							}
							'ns0:customized_field' {
								'datatype:field_code' 'PO_DETAIL_GF_15'
								'datatype:field_value' currentOrderLine.'..'.'mode-of-packing'.text()
							}
							'ns0:customized_field' {
								'datatype:field_code' 'PO_DETAIL_GF_18'
								'datatype:field_value' currentOrderLine.'..'.'corporate-brand-id'.text()
							}
							'ns0:customized_field' {
								'datatype:field_code' 'PO_DETAIL_GF_20'
								'datatype:field_value' currentOrderLine.'potential-partial-delivery'.text()
							}
							'ns0:customized_field' {
								'datatype:field_code' 'PO_DETAIL_GF_21'
								'datatype:field_value' currentOrderLine.'partial-delivery-item-quantity'.text()
							}
							'ns0:customized_field' {
								'datatype:field_code' 'PO_DETAIL_GF_23'
								'datatype:field_value' currentOrderLine.'..'.'corporate-brand-name'.text()
							}
							'ns0:item_remark' {
								'datatype:remark_code' 'HM_ITEM_ID'
								'datatype:remark_text' currentOrderLine.'id'.text()
							}

						}
					}
				}


				//customized field
				'ns0:customized_field' {
					'datatype:field_code' 'PO_HEADER_GF_04'
					'datatype:field_value' currentOrder.'production-unit'.'id'.text()
				}
				'ns0:customized_field' {
					'datatype:field_code' 'PO_HEADER_GF_05'
					'datatype:field_value' currentOrder.'corporate-brand-id'.text()
				}
				'ns0:customized_field' {
					'datatype:field_code' 'PO_HEADER_GF_06'
					'datatype:field_value' currentOrder.'corporate-brand-name'.text()
				}
				'ns0:customized_field' {
					'datatype:field_code' 'PO_HEADER_GF_10'
					'datatype:field_value' currentOrder.'order-type'.text()
				}
				'ns0:customized_field' {
					'datatype:field_code' 'PO_HEADER_GF_11'
					'datatype:field_value' currentOrder.'index'.text()
				}
				'ns0:customized_field' {
					'datatype:field_code' 'PO_HEADER_GF_12'
					'datatype:field_value' currentOrder.'sub-index'.text()
				}
				'ns0:customized_field' {
					'datatype:field_code' 'PO_HEADER_GF_19'
					'datatype:field_value' currentOrder.'season-display-name'.text()
				}
				'ns0:customized_field' {
					'datatype:field_code' 'PO_HEADER_GF_14'
					'datatype:field_value' currentOrder.'price-currency-iso-code'.text()
				}
				def vFlashPoint = currentOrder.'product'.'hazardous-materials'.'flash-point'.text()
				if (vFlashPoint != null && vFlashPoint.length()>0) {
					'ns0:flash_point' vFlashPoint
				}
				def vProdContentVol = currentOrder.'product'.'content-volume'.text()
				if (vProdContentVol != null && vProdContentVol.length()>0) {
					'ns0:po_measurement' {
						'datatype:vol' vProdContentVol
						'datatype:vol_unit_code' currentOrder.'product'.'content-volume-uom'.text()
					}
				}

			}
		}
		//clean up filtering data
		listPo.clear()

		// NCG Order Template
		order_message.'non-commercial-order'.each { currentNonCommercialOrder ->
			'ns0:po_msg_root' {
				'ns0:msg_header' {
					'header:SENDER_ID' 'HMNCG'
					'header:RECIPIENT_ID' 'POP'
					'header:MSG_CTRL_REF_NUM' '##H_AND_M_PO_CTRL_NUM##'
					'header:MSG_SENT_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
					'header:MSG_STATUS' 'N'
					'header:MSG_TYPE' 'PO'
					'header:MSG_DEF_VER' '2.01'
					'header:data_header' {
						'header:to' 'POP'
						'header:from' 'HMNCG'
						'header:time_stamp' (currentNonCommercialOrder.'order-issuing-date'.text() + "T00:00:00")
						'header:header_msg_type' 'PO'
						'header:body_msg_type' 'PO'
						'header:msg_ver' '2.01'
					}
				}
				'ns0:ext_po_num' currentNonCommercialOrder.'order-number'.text()
				'ns0:po_num' currentNonCommercialOrder.'order-number'.text()
				'ns0:cust' {}
				'ns0:csgn' {
					'ns0:div' '1010'
					'ns0:dept' currentNonCommercialOrder.'corporate-brand-id'.text()
				}
				'ns0:byr' {
					'datatype:PARTNER_NAME' currentNonCommercialOrder.'business-area'.text()
					'datatype:ADDR' {
						'datatype:CONTACT' {
							'datatype:CONTACT_TYPE' 'TEL'
							'datatype:CONTACT_NUM' currentNonCommercialOrder.'order-responsible'.'phone-number'.text()
							'datatype:email' currentNonCommercialOrder.'order-responsible'.'email'.text()
							'datatype:first_name' currentNonCommercialOrder.'order-responsible'.'first-name'.text()
							'datatype:last_name' currentNonCommercialOrder.'order-responsible'.'last-name'.text()
						}
					}
				}
				//mfr
				'ns0:mfr' {
					'datatype:PARTNER_NAME' currentNonCommercialOrder.'production-unit-name'.text()
					'datatype:PARTNER_EXT_REF_NUM' currentNonCommercialOrder.'production-unit-id'.text()
				}
				'ns0:vdr' {
					'datatype:PARTNER_NAME' currentNonCommercialOrder.'supplier-name'.text()
					'datatype:PARTNER_EXT_REF_NUM' currentNonCommercialOrder.'supplier-id'.text()
				}
				'ns0:po_attr' {
					def vOrdSts = currentNonCommercialOrder.'order-status'.text()
					'datatype:data_status_code' getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'HMOrderStatus-OLLDataStatus', vOrdSts, 'STATUS_ACTIVE', conn)
					'datatype:crt_date' {
						'datatype:record_datetime' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
					}
					'datatype:issue_date' {
						'datatype:record_datetime' currentNonCommercialOrder.'order-issuing-date'.text() + 'T00:00:00'
					}
				}

				'ns0:cust_event_ref' currentNonCommercialOrder.'corporate-brand-name'.text()

				def vSupplierDeliveryDate = currentNonCommercialOrder.'supplier-delivery-date'.text()  + 'T00:00:00'
				def vHMReceptionDate = currentNonCommercialOrder.'hm-reception-date'.text()
				//loop for order-line
				currentNonCommercialOrder.'order-line'.each { currentNonCommercialOrderLine ->
					'ns0:po_item' {
						'datatype:sku' {
							'datatype:STYLE_CODE' currentNonCommercialOrderLine.'product'.'number'.text()
							'datatype:COLOR_CODE' currentNonCommercialOrderLine.'..'.'place-of-loading'.text()
							'datatype:SIZE_CODE' vHMReceptionDate
							'datatype:DESC' currentNonCommercialOrderLine.'product'.'name'.text()
						}
						'datatype:lowest_uom_qty' currentNonCommercialOrderLine.'quantity'.text()
						'datatype:final_receiver' {
							'datatype:PARTNER_NAME' currentNonCommercialOrderLine.'..'.'delivery-info'.'site-code'.text()
							'datatype:PARTNER_EXT_REF_NUM' currentNonCommercialOrderLine.'..'.'delivery-info'.'site-code'.text()
						}

						'ns0:ship_windows' {
							'datatype:por_etd_date' {  'datatype:record_datetime' vSupplierDeliveryDate  }
							'datatype:earliest_pol_etd_date' { 'datatype:record_datetime' vSupplierDeliveryDate }
							'datatype:target_pol_etd_date' { 'datatype:record_datetime' vSupplierDeliveryDate }
							if (vHMReceptionDate!=null && vHMReceptionDate.length()>0) {
								'datatype:fnd_eta_date' { 'datatype:record_datetime' vHMReceptionDate + 'T00:00:00' }
							}
						}
						//here
						'ns0:origin' {
							'datatype:UNLOCODE' currentNonCommercialOrderLine.'..'.'place-of-loading'.text()
							'datatype:cust_ref' currentNonCommercialOrderLine.'..'.'place-of-loading'.text()
						}
						'ns0:fnd' {
							'datatype:LOC_NAME' currentNonCommercialOrderLine.'..'.'delivery-info'.'country-name'.text()
							'datatype:cust_ref' currentNonCommercialOrderLine.'..'.'delivery-info'.'country-code'.text()
						}
						'ns0:ship_via' {
							def vMOT = currentNonCommercialOrderLine.'..'.'mode-of-transport'.text()
							'datatype:ship_via_code' getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'HMshipviacode-ollshipviacode', vMOT, '', conn)
							'datatype:ship_via_ext_ref' vMOT
						}
						'ns0:cust_whs' {
							'datatype:PARTNER_EXT_REF_NUM' currentNonCommercialOrderLine.'..'.'delivery-info'.'country-code'.text()
						}

						'ns0:customized_field' {
							'datatype:field_code' 'PO_DETAIL_GF_01'
							'datatype:field_value' currentNonCommercialOrderLine.'..'.'corporate-brand'.text()
						}
						'ns0:customized_field' {
							'datatype:field_code' 'PO_DETAIL_GF_02'
							'datatype:field_value' currentNonCommercialOrderLine.'..'.'terms-of-delivery'.text()
						}
						'ns0:customized_field' {
							'datatype:field_code' 'PO_DETAIL_GF_18'
							'datatype:field_value' currentNonCommercialOrderLine.'..'.'corporate-brand-id'.text()
						}
						'ns0:customized_field' {
							'datatype:field_code' 'PO_DETAIL_GF_23'
							'datatype:field_value' currentNonCommercialOrderLine.'..'.'corporate-brand-name'.text()
						}
					}
				}

				//customized field
				'ns0:customized_field' {
					'datatype:field_code' 'PO_HEADER_GF_22'
					'datatype:field_value' currentNonCommercialOrder.'business-area'.text()
				}
				'ns0:customized_field' {
					'datatype:field_code' 'PO_HEADER_GF_24'
					'datatype:field_value' currentNonCommercialOrder.'project-number'.text()
				}
				'ns0:customized_field' {
					'datatype:field_code' 'PO_HEADER_GF_25'
					'datatype:field_value' currentNonCommercialOrder.'campaign-number'.text()
				}
				'ns0:customized_field' {
					'datatype:field_code' 'PO_HEADER_GF_05'
					'datatype:field_value' currentNonCommercialOrder.'corporate-brand-id'.text()
				}
				'ns0:customized_field' {
					'datatype:field_code' 'PO_HEADER_GF_06'
					'datatype:field_value' currentNonCommercialOrder.'corporate-brand-name'.text()
				}
			}
			//end of NGC Order template
		}
	}
	//mapping logic end
	//def outxml = XmlUtil.serialize(writer.toString());
	//println outxml;

	//println "finished: "+currentSystemDt.format("yyyy-MM-dd HH:mm:ss.SSS")

	//return outxml;

	return writer.toString();

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

