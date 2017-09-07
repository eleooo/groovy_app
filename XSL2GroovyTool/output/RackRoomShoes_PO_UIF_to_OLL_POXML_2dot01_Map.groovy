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
import java.text.SimpleDateFormat;
import java.util.Date
import java.util.TimeZone

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
	def RackRoomShoes_PO_UIF_Root = new XmlParser().parseText(inputXmlBody);//XmlSlurper
	
	def writer = new StringWriter()
	def outXml = new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false)
	//def xmlBuilder = new StreamingMarkupBuilder()
	
	//mapping script

	TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
	def currentSystemDt = new Date()
	
	def uniqueId = RackRoomShoes_PO_UIF_Root.ECOM_Header.Unique_ID.text()
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
			'header:SENDER_ID' 'RACKROOMSHOES'
			'header:RECIPIENT_ID' 'POP'
			'header:MSG_CTRL_REF_NUM' uniqueId
			'header:MSG_SENT_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
			'header:MSG_REC_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
			'header:MSG_STATUS' 'N'
			'header:MSG_TYPE' 'PO'
			'header:MSG_DEF_VER' '2.01'
		}
		'ns0:po_msg_root' {
			'ns0:msg_header' {
				'header:SENDER_ID' 'RACKROOMSHOES'
				'header:RECIPIENT_ID' 'POP'
				'header:MSG_CTRL_REF_NUM' uniqueId
				'header:MSG_SENT_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")				
				'header:MSG_STATUS' 'N'
				'header:MSG_TYPE' 'PO'
				'header:MSG_DEF_VER' '2.01'
				'header:data_header' {
					'header:to' 'POP'
					'header:from' RackRoomShoes_PO_UIF_Root.ECOM_Header.Senders_ECom_ASCII_Communication_ID.text()
					'header:time_stamp' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
					'header:header_msg_type' 'PO'
					'header:body_msg_type' 'PO'
					'header:msg_ver' '2.01'
				}
			}
			'ns0:ext_po_num' RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Purchase_Order_No.text()
			'ns0:po_num' RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Purchase_Order_No.text()
			'ns0:cust' {
				'datatype:PARTNER_EXT_REF_NUM' RackRoomShoes_PO_UIF_Root.ECOM_Header.Senders_ECom_ASCII_Communication_ID.text()
			}
			'ns0:csgn' {
				'datatype:PARTNER_EXT_REF_NUM' RackRoomShoes_PO_UIF_Root.ECOM_Header.Senders_ECom_ASCII_Communication_ID.text()
				'ns0:dept' '0001'
			}
			'ns0:byr' {
				'datatype:PARTNER_EXT_REF_NUM' RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Ship_To_Code.text()
			}
			//mfr
			RackRoomShoes_PO_UIF_Root.PO_Body.PO_SubHeader.each { currentSubHeader ->
				'ns0:mfr' {
					'datatype:PARTNER_NAME' currentSubHeader.Factory_Name.text()
					'datatype:PARTNER_EXT_REF_NUM' currentSubHeader.Factory_ID.text()
					'datatype:ADDR' {
						'datatype:ADDR_LINE' currentSubHeader.Factory_Address_Line_1.text()
						'datatype:ADDR_LINE' currentSubHeader.Factory_Address_Line_2.text()
						def vAdd3 = currentSubHeader.Factory_City.text()
						def vZip = currentSubHeader.Factory_Zip_Code.text()
						def vCountry = currentSubHeader.Factory_Country.text()
						if (vZip!=null && vZip.length()>0) {
							vAdd3 += " "+vZip
						}
						if (vCountry!=null && vCountry.length()>0) {
							vAdd3 += " "+vCountry
						}
						'datatype:ADDR_LINE' vAdd3
					}
				}
			}
			//vdr
			RackRoomShoes_PO_UIF_Root.PO_Body.PO_SubHeader.each { currentSubHeader ->
				'ns0:vdr' {
					'datatype:PARTNER_NAME' currentSubHeader.Vendor_Name.text()
					'datatype:PARTNER_EXT_REF_NUM' currentSubHeader.Vendor_ID.text()
					'datatype:ADDR' {
						'datatype:ADDR_LINE' currentSubHeader.Vendor_Address_Line_1.text()
						'datatype:ADDR_LINE' currentSubHeader.Vendor_Address_Line_2.text()
						def vAdd3 = currentSubHeader.Vendor_City.text()						
						def vState = currentSubHeader.Vendor_State.text()
						def vZip = currentSubHeader.Vendor_Zip_Code.text()
						def vCountry = currentSubHeader.Vendor_Country.text()
						if (vState!=null && vState.length()>0) {
							vAdd3 += " "+vState
						}
						if (vZip!=null && vZip.length()>0) {
							vAdd3 += " "+vZip
						}
						if (vCountry!=null && vCountry.length()>0) {
							vAdd3 += " "+vCountry
						}
						'datatype:ADDR_LINE' vAdd3
					}
				}
			}
			//po_attr
			'ns0:po_attr' {
				def vOrderType = RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Purchase_Order_Type.text()
				vOrderType = getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'RSS_PO_Type-OLLDataStatus', vOrderType, 'STATUS_ACTIVE', conn)
				'datatype:data_status_code' vOrderType
				'datatype:issue_date' {
					'datatype:record_datetime' convertDateTime(RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Purchase_Order_Date.text(), 'yyyyMMdd', 'yyyy-MM-dd\'T00:00:00\'')
				}
				def vCancelDt = RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Cancel_Date.text()
				if (vCancelDt!=null && vCancelDt.length()>0) {
					vCancelDt = convertDateTime(vCancelDt, 'yyyyMMdd', 'yyyy-MM-dd\'T00:00:00\'')
					'datatype:cancel_date' {
						'datatype:record_datetime' vCancelDt
					}
				}
			}
			'ns0:lc_num' RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Shipment_Method_Of_Payment.text()
			'ns0:ship_via' {
				'datatype:ship_via_code' 'S'
			}
			'ns0:por' {
				'datatype:cust_ref' RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Export_Country_Code.text()
			}
			'ns0:fnd' {
				'datatype:cust_ref' RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Ship_To_Code.text()
			}
			'ns0:ship_window' {
				def vEarliestShipDate = RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Earliest_Ship_Date.text()
				if (vEarliestShipDate!=null && vEarliestShipDate.length()>0) {
					'datatype:earliest_pol_etd_date' {
						'datatype:record_datetime' convertDateTime(vEarliestShipDate, 'yyyyMMdd', 'yyyy-MM-dd\'T00:00:00\'')
					}
				}
				//
				def vLastestShipDateFOBDate = RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Latest_Ship_Date_FOB_Date.text()
				if (vLastestShipDateFOBDate!=null && vLastestShipDateFOBDate.length()>0) {
					'datatype:latest_pol_etd_date' {
						'datatype:record_datetime' convertDateTime(vLastestShipDateFOBDate, 'yyyyMMdd', 'yyyy-MM-dd\'T00:00:00\'')
					}
				}
				//
				def vDeliveryDate = RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Delivery_Date.text()
				if (vDeliveryDate!=null && vDeliveryDate.length()>0) {
					'datatype:fnd_eta_date' {
						'datatype:record_datetime' convertDateTime(vDeliveryDate, 'yyyyMMdd', 'yyyy-MM-dd\'T00:00:00\'')
					}
				}
			}
			'ns0:cust_whs' {
				'datatype:PARTNER_NAME' RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Warehouse_Code.text()
				'datatype:PARTNER_EXT_REF_NUM' RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Warehouse_Code.text()
			}
			'ns0:country_of_origin' {
				'datatype:cust_ref' RackRoomShoes_PO_UIF_Root.PO_Body.PO_Header.Origin_Country_Code.text()
			}
			//each line
			RackRoomShoes_PO_UIF_Root.PO_Body.Line.eachWithIndex { currentLine, index ->
				'ns0:po_item' {
					'datatype:item_num' index + 1
					'datatype:sku' {
						'datatype:STYLE_CODE' currentLine.PO_Line.SKU_No.text()
						'datatype:COLOR_CODE' currentLine.PO_Line.SKU_Color.text()
						//def allSubLine = currentLine.PO_SubLine.findAll();
						if (currentLine.PO_SubLine.size()==0) {
							def vSkuSizeWidth = currentLine.PO_Line.'Rackroom_SKU-Size-Width'.text()
							def vBarcodeSize = currentLine.PO_Line.Barcode_Size.text()
							def vBarcodeWidth = currentLine.PO_Line.Barcode_Width.text()
							def vSkuNo = currentLine.PO_Line.SKU_No.text()
							
							'datatype:SIZE_CODE' vSkuSizeWidth.substring(6) + "  -" + vBarcodeSize + vBarcodeWidth + vSkuNo
						}
						'datatype:DESC' currentLine.PO_Line.Label_Description.text()
					}
					'datatype:outer_uom' {
						'datatype:uom_ext_ref' currentLine.PO_Line.Case_Type_Code.text()
					}
					def vQty = currentLine.PO_Line.Quantity.text() as double
					def vCaseType = currentLine.PO_Line.Case_Type_Code.text()
					def vPackSize = currentLine.PO_Line.'Pre-Pack_Case_Size'.text() as double
					def vOuterUomQty = vQty
					if (vCaseType!=null && !vCaseType.equalsIgnoreCase('CASE') && vPackSize>0) {
						vOuterUomQty = vQty / vPackSize.toDouble()
					}
					'datatype:outer_uom_qty' vOuterUomQty.toInteger()
					
					'datatype:lowest_uom' {
						'datatype:uom_ext_ref' 'EA'
					}
					def vlowestUomQty = vQty
					if (vCaseType!=null && vCaseType.equalsIgnoreCase('CASE')) {
						vlowestUomQty = vQty * vPackSize
					}
					'datatype:lowest_uom_qty' vlowestUomQty.toInteger()
					//loop subLine
					currentLine.PO_SubLine.eachWithIndex { currentSubline, indexSubline ->
						'datatype:inner_pack_detail' {
							'datatype:inner_item_num' (indexSubline + 1)
							'datatype:inner_sku' {
								def vUpc = currentSubline.Rackroom_UPC.text()
								def vBarcodeSize = currentSubline.Barcode_Size.text()
								def vBarcodeWidth = currentSubline.Barcode_Width.text()
								def vPOLineSkuNo = currentLine.PO_Line.SKU_No.text()
								'datatype:SIZE_CODE' vUpc.substring(6) + "  -" + vBarcodeSize + vBarcodeWidth + vPOLineSkuNo
							}
							'datatype:inner_outer_uom_qty' currentSubline.Quantity.text()
							'datatype:inner_outer_uom' {
								'datatype:uom_ext_ref' 'EA'
							}
							'datatype:inner_lowest_uom_qty' currentSubline.Quantity.text() as int
							'datatype:inner_lowest_uom' {
								'datatype:uom_ext_ref' 'EA'
							}
							'datatype:inner_lowest_uom_price' {
								//output element cur_code with attribute nil=true
								'datatype:cur_code' ('xsi:nil':'true')
								'datatype:amt' currentLine.PO_Line.First_Cost.text() as double
							}
							'datatype:upc_ref' currentSubline.Rackroom_UPC.text()
						}
					}
					//end loop
					'ns0:vdr_sku' {
						'datatype:STYLE_CODE' currentLine.PO_Line.SKU_Style.text()
					}
					'ns0:lowest_uom_cost' {
						'datatype:cur_code' ('xsi:nil':'true')
						'datatype:amt' (currentLine.PO_Line.First_Cost.text() as double)
					}
					'ns0:lowest_uom_price' {
						'datatype:cur_code' ('xsi:nil':'true')
						def vOutPrice = (currentLine.PO_Line.Our_Price.text() as double) / 100
						'datatype:amt' vOutPrice.round(2)
					}
					if (currentLine.PO_SubLine.size()==0) {
						'ns0:upc_ref' currentLine.PO_Line.'Rackroom_SKU-Size-Width'.text()
					}
					'ns0:loc_obj' {
						'datatype:LOC_TYPE' 'COUNTRY_OF_ORIGIN'
						def vOriginCntyCde = currentLine.'..'.PO_Header.Origin_Country_Code.text()
						'datatype:LOC_NAME' vOriginCntyCde
						'datatype:COUNTRY_NAME' vOriginCntyCde
						'datatype:cust_ref' vOriginCntyCde
					}
				}
			}
		}
	}
	//mapping logic end
	//def endDt = new Date()
	//println 'cost: '+(endDt.getTime() - currentSystemDt.getTime()) +' ms.' 
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