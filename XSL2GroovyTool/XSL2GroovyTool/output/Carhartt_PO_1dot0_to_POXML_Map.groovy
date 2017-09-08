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
import java.lang.ref.ReferenceQueue.Null;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date
import java.util.TimeZone

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


	byte[] bs = inputXmlBody.getBytes();
	def instr = inputXmlBody

	//Important: the inputXml is xml root element
	def inXml = new XmlParser().parseText(instr); //XmlSlurper

	TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
	def currentSystemDt = new Date()
	def current_dateTime = currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")

	//	println "-------------- session: "+ollSessionId+" start mapping at: "+currentSystemDt.format("yyyy-MM-dd HH:mm:ss.SSS")

	outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")



	//mapping logic start
	outXml.'ns0:po_file_root'('xmlns:datatype':'http://www.oocllogistics.com/datatype', 'xmlns:header':'http://www.oocllogistics.com/header', 'xmlns:notelog':'http://www.oocllogistics.com/notelog', 'xmlns:ns0':'http://www.oocllogistics.com/po', 'xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance', 'xmlns:xsl':'http://www.w3.org/1999/XSL/Transform')
	{
		'ns0:file_header'{
			'header:SENDER_ID' 'CARHARTT'
			'header:RECIPIENT_ID' 'POP'

			/*
			 'header:MSG_CTRL_REF_NUM' uniqueId
			 */

			'header:MSG_CTRL_REF_NUM' currentSystemDt.format("yyyyMMdd")


			'header:MSG_SENT_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
			'header:MSG_REC_DATETIME' currentSystemDt.format("yyyy-MM-dd'T'HH:mm:ss")
			'header:MSG_STATUS' 'N'
			'header:MSG_TYPE' 'PO'
			'header:MSG_DEF_VER' '2.01'
		}

		//println inXml


		inXml.orderDetail.each { currentorderDetail ->


			'ns0:po_msg_root'{

				def var_v13 = currentorderDetail.orderFunctionCode.text()




				def var_v20=!((currentorderDetail.orderTerms.shipwindowbegin.text().equals("")))

				'ns0:msg_header'{
					def var_v6 = "CARHARTT"
					'header:SENDER_ID' var_v6
					'header:RECIPIENT_ID' 'POP'

					'header:MSG_CTRL_REF_NUM' currentSystemDt.format("yyyyMMdd")
					def var_v9 = current_dateTime
					'header:MSG_SENT_DATETIME' var_v9
					def var_v10 = getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID,"Carhartt_PO_OrderFunction-OLLMsgStatus" , string(currentorderDetail.orderFunctionCode.text()) , "N" ,conn)
					'header:MSG_STATUS' var_v10
					'header:MSG_TYPE' 'PO'
					'header:MSG_DEF_VER' '2.01'
					'header:data_header'{
						'header:to' 'POP'
						def var_v11 = "CARHARTT"
						'header:from' var_v11
						def var_v12 = current_dateTime
						'header:time_stamp' var_v12
						'header:header_msg_type' 'PO'
						'header:body_msg_type' 'PO'
						'header:msg_ver' '2.01'
					}
				}
				'ns0:ext_po_num' currentorderDetail.poNumber.text()
				'ns0:po_num' currentorderDetail.poNumber.text()
				'ns0:cust'{ 'datatype:PARTNER_NAME' 'CARHARTT' }
				'ns0:csgn'{ 'datatype:PARTNER_NAME' 'CARHARTT' }
				'ns0:byr'{
					'datatype:PARTNER_NAME' currentorderDetail.orderIdentification.buyerIdentification.buyerName.text()
					'datatype:PARTNER_EXT_REF_NUM' currentorderDetail.orderIdentification.buyerIdentification.buyerCode.text()
					'datatype:ADDR'{
						'datatype:country_name' currentorderDetail.orderIdentification.buyerIdentification.buyerCtry.text()
						'datatype:state_code' currentorderDetail.orderIdentification.buyerIdentification.buyerStateCd.text()
						'datatype:city'{
							'datatype:LOC_NAME' currentorderDetail.orderIdentification.buyerIdentification.buyerCity.text()
						}
						'datatype:postal_code' currentorderDetail.orderIdentification.buyerIdentification.buyerZip.text()
						'datatype:ADDR_LINE' currentorderDetail.orderIdentification.buyerIdentification.buyerAdd1.text()
						if(currentorderDetail.orderIdentification.buyerIdentification.buyerAdd2){
							'datatype:ADDR_LINE' currentorderDetail.orderIdentification.buyerIdentification.buyerAdd2.text()
						}
						if(currentorderDetail.orderIdentification.buyerIdentification.buyerAdd3){
							'datatype:ADDR_LINE' currentorderDetail.orderIdentification.buyerIdentification.buyerAdd3.text()
						}
					}
				}
				'ns0:vdr'{
					'datatype:PARTNER_NAME' currentorderDetail.orderIdentification.sellerIdentification.sellerName.text()
					'datatype:PARTNER_EXT_REF_NUM' currentorderDetail.orderIdentification.sellerIdentification.sellerCode.text()
					'datatype:ADDR'{
						if(currentorderDetail.orderIdentification.sellerIdentification.sellerState){
							'datatype:state_name' currentorderDetail.orderIdentification.sellerIdentification.sellerState.text()
						}
						'datatype:city'{
							if(currentorderDetail.orderIdentification.sellerIdentification.sellerCity){
								'datatype:LOC_NAME' currentorderDetail.orderIdentification.sellerIdentification.sellerCity.text()
							}
							if(currentorderDetail.orderIdentification.sellerIdentification.sellerCtry){
								'datatype:COUNTRY_NAME' currentorderDetail.orderIdentification.sellerIdentification.sellerCtry.text()
							}
						}
						if(currentorderDetail.orderIdentification.sellerIdentification.sellerZip){
							'datatype:postal_code' currentorderDetail.orderIdentification.sellerIdentification.sellerZip.text()
						}
						'datatype:ADDR_LINE' currentorderDetail.orderIdentification.sellerIdentification.sellerAdd1.text()
						if(currentorderDetail.orderIdentification.sellerIdentification.sellerAdd2){
							'datatype:ADDR_LINE' currentorderDetail.orderIdentification.sellerIdentification.sellerAdd2.text()
						}
						if(currentorderDetail.orderIdentification.sellerIdentification.sellerAdd3){
							'datatype:ADDR_LINE' currentorderDetail.orderIdentification.sellerIdentification.sellerAdd3.text()
						}
					}
				}
				'ns0:po_attr'{
					//  def var_v14 = getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID,"Carhartt_PO_OrderFunction-OLLMsgDataStatusCode" , var_v13 , "STATUS_ACTIVE" ,conn)

					def var_v14 = 'C'

					'datatype:data_status_code' var_v14

					def orderIssueDate=string(currentorderDetail.orderTerms.issueDate.text())

					//	 def var_v15 = ConvertDatetimeFormat(string(orderIssueDate) , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00'")
					def var_v15 = convertDateTime(string(orderIssueDate) , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00'")
					//	def var_v15 ="2016-11-11T00:00:00"

					def var_v16 = !(string(var_v15).equals(""))
					if(var_v16){
						def var_v17 = string(currentorderDetail.orderTerms.issueDate.text())
						'datatype:issue_date'{
							def var_v18 = convertDateTime(var_v17 , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00'")
							//	def var_v18="2016-11-11T00:00:00"
							'datatype:record_datetime' var_v18
						}
					}
				}
				currentorderDetail.orderTerms.paymentterms.each { pTerms ->

					if(pTerms!=null && pTerms.text()!=""){
						'ns0:payment_method'{ 'datatype:payment_method_code' pTerms }
					}
				}
				'ns0:ship_via'{
					'datatype:ship_via_ext_ref' currentorderDetail.orderTerms.shipmentMethodCode.text()
				}
				'ns0:por'{
					'datatype:cust_ref' currentorderDetail.orderTerms.incotermLocationCode.text()
				}
				def ShipmentDestinationName = (currentorderDetail.orderTerms.shipmentDestination.destName.text())
				def ShipmentDestinationKey = (currentorderDetail.orderTerms.shipmentDestination.destinationKey.text())
				def FinalDestination = (currentorderDetail.orderTerms.shipmentFinalDestination.text())


				def FNDName
				if(FinalDestination!='')
				{
					FNDName=FinalDestination
				}
				else
				{
					FNDName=ShipmentDestinationName
				}


				def FNDCode
				if(FinalDestination!='')
				{
					FNDCode=FinalDestination
				}
				else
				{
					FNDCode=ShipmentDestinationKey
				}




				'ns0:fnd'{
					'datatype:LOC_NAME' FNDName
					'datatype:cust_ref' FNDCode
				}
				'ns0:ship_window'{

					def var_v21 = string(currentorderDetail.orderTerms.shipwindowbegin.text())
					if(var_v21!=null && var_v21!=''){

						'datatype:earliest_pol_etd_date'{
							def var_v22 =convertDateTime(var_v21, 'yyyyMMdd', 'yyyy-MM-dd\'T00:00:00\'')

							'datatype:record_datetime' var_v22
						}
					}
					currentorderDetail.orderTerms.reference.each { pReference ->

						if(pReference.type.text() == "RECEIVE_DT")
						{
							String var_v24 = convertDateTime((pReference.value.text()) , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00'")
							String var_v26 = convertDateTime(pReference.value.text() , "yyyy-MM-dd" , "yyyy-MM-dd'T00:00:00'")

							def var_v32 = var_v24
							if(var_v32=="")
							{
								var_v32=var_v26
							}



							if(var_v32!=null && var_v32!=''){
								'datatype:target_pol_etd_date'{ 'datatype:record_datetime' var_v32 }
							}
						}
					}


				}
				'ns0:incoterm'{
					'datatype:intcoterm_code' currentorderDetail.orderTerms.incotermCode.text()
					'datatype:intcoterm_name' currentorderDetail.orderTerms.incotermCode.text()
					'datatype:intcoterm_ext_ref' currentorderDetail.orderTerms.incotermCode.text()
				}

				currentorderDetail.orderTerms.reference.each { pReference ->
					def var_v33 = string(pReference.type.text())
					def var_v34 = (var_v33.equals("SEASON"))
					if(var_v34){
						'ns0:cust_event_ref' pReference.value.text()
					}
				}
				'ns0:ship_to'{
					'datatype:PARTNER_NAME' currentorderDetail.orderTerms.shipmentDestination.destName.text()
					'datatype:ADDR'{
						'datatype:country_name' currentorderDetail.orderTerms.shipmentDestination.destctrycd.text()
						'datatype:state_name' currentorderDetail.orderTerms.shipmentDestination.deststate.text()
						'datatype:city'{
							'datatype:LOC_NAME' currentorderDetail.orderTerms.shipmentDestination.destcity.text()
							'datatype:COUNTRY_NAME' currentorderDetail.orderTerms.shipmentDestination.destctrycd.text()
						}

						'datatype:postal_code' currentorderDetail.orderTerms.shipmentDestination.destzip.text()
						'datatype:ADDR_LINE' currentorderDetail.orderTerms.shipmentDestination.destAdd1.text()
						'datatype:ADDR_LINE' currentorderDetail.orderTerms.shipmentDestination.destAdd2.text()
						'datatype:ADDR_LINE' currentorderDetail.orderTerms.shipmentDestination.destAdd3.text()
					}
				}




				currentorderDetail.orderLineItem.eachWithIndex { pItemNumber,index ->


					def var_v35 = ((pItemNumber.itemTypeCode.text().toUpperCase()))
					def var_v36 = ("SUB" == string(var_v35))
					if(var_v36)
					{
						def var_v40 = LogicalNe(string(pItemNumber.StandardCaseQty.text()) , "")



						def var_v43,var_v53

						if(pItemNumber.baseLineItem!=null && pItemNumber.baseLineItem[0]!=null && pItemNumber.baseLineItem[0].quantity!=null)
						{



							//	var_v43 = ceiling(pItemNumber.baseLineItem[0].quantity.text())
							var_v43 = (pItemNumber.baseLineItem[0].quantity.text())
							// var_v53 = string(pItemNumber.baseLineItem[0].quantity.text())
							var_v53 = (pItemNumber.baseLineItem[0].quantity.text())
						}
						else
						{
							var_v43=0
							var_v53=0
						}




						def var_v54 = ceiling(var_v53)


						'ns0:po_item'{

							def dItemSeqNumber
							if(pItemNumber.baseLineItem[0].itemSequenceNumber!=null)
							{
								dItemSeqNumber=pItemNumber.baseLineItem[0].itemSequenceNumber.text()
							}
							else
							{
								dItemSeqNumber=''
							}



							'datatype:item_num' dItemSeqNumber


							'datatype:sku'{
								'datatype:STYLE_CODE' pItemNumber.parentItemKey.text()
								pItemNumber.baseLineItem[0].itemReference.each { pItemReference ->
									def var_v37 = string(pItemReference.type.text())
									def var_v38 = StringUpperCase(var_v37)
									def var_v39 =("SIZE" == string(var_v38))
									if(var_v39){
										'datatype:SIZE_CODE' pItemReference.value.text()
									}
								}
								def paramHTSDEsc = string(pItemNumber.HTS_Code_Desc.text())
								def paramParentItemKey = string(pItemNumber.parentItemKey.text())
								if(paramHTSDEsc!=''){
									'datatype:DESC' paramHTSDEsc
								}
								if(paramHTSDEsc==''){

									def allOrders = currentorderDetail.orderLineItem.findAll{it.'itemTypeCode'.text() == 'Main' && it.'itemKey'.text() == paramParentItemKey}


									def desc=""
									if(allOrders!=null && allOrders.size()>0 && allOrders[0].baseLineItem.shortDescription!=null)
									{
										desc=allOrders[0].baseLineItem.shortDescription.text()
									}
									else
									{
										desc=""
									}

									'datatype:DESC' desc
								}
							}


							if(var_v40){
								def var_v44 = string(pItemNumber.StandardCaseQty.text())
								def var_v45 = getInt(var_v44)


								'datatype:pack_factor' var_v45
							}
							def var_v44 = getInt(string(var_v43))
							'datatype:lowest_uom_qty' var_v44



							'ns0:ship_windows'{
								pItemNumber.baseLineItem[0].value.each { pBaseLineItemValue ->

									if(pBaseLineItemValue!=null && pBaseLineItemValue!="")
									{
										String var_v45 = pBaseLineItemValue.text();
										def var_v46 = convertDateTime(var_v45 , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00'")

										def var_v47 = LogicalNe(string(var_v46) , "")
										if(var_v47){
											'datatype:target_pol_etd_date'{
												def var_v48 = convertDateTime(var_v45 , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00'")

												'datatype:record_datetime' var_v48
											}
										}
									}

								}



								pItemNumber.baseLineItem[0].atpdate.each { pAtpdate ->
									String var_v49 = pAtpdate.text()
									def var_v50 =convertDateTime(var_v49 , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00'")


									def var_v51 = LogicalNe(string(var_v50) , "")
									if(var_v51){
										'datatype:fnd_eta_date'{
											def var_v52 = convertDateTime(var_v49 , "yyyyMMdd" , "yyyy-MM-dd'T00:00:00'")

											'datatype:record_datetime' var_v52
										}
									}
								}
							}

							def deBuyerName
							if(pItemNumber.baseLineItem[0]!=null && pItemNumber.baseLineItem[0].buyerNumber!=null)
							{
								deBuyerName=pItemNumber.baseLineItem[0].buyerNumber.text()
							}
							else
							{
								deBuyerName=""
							}

							'ns0:vdr_sku'{ 'datatype:STYLE_CODE' deBuyerName }

							'ns0:item_attr'{
								def var_v55 = ReturnItemStatus(string(var_v54))
								'datatype:data_status_code' var_v55
							}
							def paramUnitPrice = string(pItemNumber.lineItemPrice.pricePerUnit.text())
							if(string_length(paramUnitPrice) > 0){
								'ns0:lowest_uom_cost'{
									'datatype:cur_code' 'USD'
									'datatype:amt' paramUnitPrice
								}
							}
							def paramUnitPrice2 = string(pItemNumber.lineItemPrice.pricePerUnit.text())
							if(string_length(paramUnitPrice) > 0){
								'ns0:lowest_uom_price'{
									'datatype:cur_code' 'USD'
									'datatype:amt' paramUnitPrice2
								}
							}
							'ns0:upc_ref' pItemNumber.UPCnumber.text()
							def smallcase = 'abcdefghijklmnopqrstuvwxyz'
							def uppercase = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'


							def sizeItem = pItemNumber.baseLineItem[0].itemReference.findAll{StringUpperCase(it.'type'.text()) == 'SIZE'}
							def dimItem = pItemNumber.baseLineItem[0].itemReference.findAll{StringUpperCase(it.'type'.text()) == 'DIM'}

							if(sizeItem!=null && sizeItem.size()>0)
							{
								'ns0:customized_field'{
									'datatype:field_code' 'PO_DETAIL_GF_06'
									'datatype:field_value' dimItem[0].value.text()+sizeItem[0].value.text()
								}
							}

							if(dimItem!=null && dimItem.size()>0)
							{
								'ns0:customized_field'{
									'datatype:field_code' 'PO_DETAIL_GF_07'
									'datatype:field_value' dimItem[0].value.text()
								}
							}

							'ns0:customized_field'{
								'datatype:field_code' 'PO_DETAIL_GF_08'
								'datatype:field_value' pItemNumber.UPCnumber1.text()
							}
						}





					}

				}



				def paramDestCity = string(currentorderDetail.orderTerms.shipmentDestination.destcity.text())
				def paramMoveType = string(currentorderDetail.orderTerms.movetype.text())
				def paramOrderType = string(inXml.Order.header.orderType.text())
				def UPCnumber1 = currentorderDetail.orderLineItem.UPCnumber1.text()
				def paramDestinationState = string(currentorderDetail.orderTerms.shipmentDestination.deststate.text())

				def seasonItem = currentorderDetail.orderTerms.reference.findAll{StringUpperCase(it.'type'.text()) == 'SEASON'}


				def sSeason =""
				if(seasonItem!=null)
				{
					sSeason =seasonItem[0].value.text()
				}

				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_01'
					'datatype:field_value' paramDestinationState
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_02'
					'datatype:field_value' paramOrderType
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_03'
					'datatype:field_value' paramDestCity
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_04'
					'datatype:field_value' paramMoveType
				}
				'ns0:customized_field'{
					'datatype:field_code' 'PO_HEADER_GF_05'
					'datatype:field_value' sSeason
				}
				currentorderDetail.orderTerms.additionalCondition.eachWithIndex { pAdditionalCondition,index ->
					def var_v56 = index+1
					def var_v57 = concat(string(pAdditionalCondition.additionalConditionText.text()) , " - " , string(pAdditionalCondition.additionalConditionAcknowledgementCode.text()))
					'ns0:po_remark'{
						'xsl:attribute'name:'xsi:type'{ 'xsl:value-of'select:'\'datatype:_remark\'' }
						'datatype:seq' var_v56
						'datatype:remark_code' 'REMARK'
						'datatype:remark_text' var_v57
					}
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

public String convertDateTime(String inputDate, String inputFormat, String outputFormat)  {
	String output = "";

	try{


		if (inputDate!=null && inputDate.trim().length()>0 && inputFormat!=null && inputFormat.trim().length()>0
		&& outputFormat!=null && outputFormat.trim().length()>0) {

			SimpleDateFormat sfmt = new SimpleDateFormat(inputFormat);
			java.util.Date date = sfmt.parse(inputDate);

			SimpleDateFormat soutfmt = new SimpleDateFormat(outputFormat);
			output = soutfmt.format(date);
		}
	}
	catch(Exception ex)
	{
		return "";
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

public String concat(def d1,def d2,def d3){
	return d1+d2+d3;
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

public def string_length(def str){
	if(null != str && str.length() > 0){
		return str.length()
	}else{
		return 0
	}
}

public def  ReturnFNDDate(String param1, String param2) {
	try {
		if (param1!='') {
			return param1;
		}
		return param2;
	} catch (Exception e) {
		return param2;
	}
}

public def LogicalNe(def p1, def p2) {
	try {
		if (p1 != null && p2 != null && !(p1.equals(p2))) {
			return true;
		} else {
			return false;
		}
	} catch (Exception ex) {
		return false;
	}
}

public def  LogicalAnd(def s1, def s2) {
	try {
		if (s1!='' && s2!='') {
			if (s1.toLowerCase().equals("true")
			&& s2.toLowerCase().equals("true")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	} catch (Exception ex) {
		return false;
	}
}

public String  ceiling(String sNum) {
	String sRet = sNum;
	double n = 0.0;
	try {
		n = tryParseString2Double(sRet);
		sRet = (int) Math.ceil(n) + "";
		return sRet;
	} catch (Exception e) {
		return sRet;
	}
}

public def  getInt(String sNum) {
	String sRet = sNum;

	if(sNum!=null && sNum!="")
		try {
			int i=Double.parseDouble(sNum)
			return i;
		} catch (Exception e) {
			return "0";
		}
}


public def tryParseString2Double(String val) {
	try {
		String[] arr = val.split("\\.");
		if (arr[0].startsWith(",")) {
			throw new Exception();
		}
		arr[0] = arr[0].replaceAll(",", "");
		String numberStr = "";
		if (arr.length == 1) {
			numberStr = arr[0];
		} else {
			numberStr = arr[0] + "." + arr[1];
		}
		double d = Double.parseDouble(numberStr);
		return d;
	} catch (Exception e) {
		return 0;
	}

}

public String  StringUpperCase(String s) {
	try {
		if (s != null && !s.equals("")) {
			return s.toUpperCase();
		} else {
			return "";
		}
	} catch (Exception ex) {
		return "";
	}
}

public String returnQTY(String sQTY) {
	try {
		if (sQTY=="") {
			return "0";
		}
		return sQTY;
	} catch (Exception e) {
		return sQTY;
	}
}

public String ReturnItemStatus(String nQty) {
	try {
		int number = Integer.parseInt(nQty);
		if (number <= 0) {
			return "STATUS_CANCELLED";
		}
		return "STATUS_ACTIVE";
	} catch (Exception ex) {
		return "STATUS_CANCELLED";
	}
}



