package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.text.SimpleDateFormat;
import java.util.Date
import java.util.List;
import java.util.Map;

// import cs.b2b.beluga.common.tools.DefinitionGeneratorFromBiztalkSchema;
import cs.b2b.core.common.util.StringUtil;
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.mapping.bean.ct.BookingGeneralInfo
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.ct.FND
import cs.b2b.core.mapping.bean.ct.FirstPOL
import cs.b2b.core.mapping.bean.ct.LastPOD
import cs.b2b.core.mapping.bean.ct.OceanLeg
import cs.b2b.core.mapping.bean.ct.POR
import cs.b2b.core.mapping.bean.ct.Party
import cs.b2b.core.mapping.util.XmlBeanParser


/**
 * @author RENGA
 * VFCGTN CT initialize on 20161115 
 */
public class CUS_CS2CTXML_315_KUBOTAYUSEN{

	cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	cs.b2b.core.mapping.util.MappingUtil_CT_O_Common ctUtil = new cs.b2b.core.mapping.util.MappingUtil_CT_O_Common(util);

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

	def currentSystemDt = null

	public String getScac_cde(String id, Connection conn) throws Exception {
		if (conn == null) {
			throw new Exception("DB Connection is not available for query. ");
		}

		if ( id==null) {
			return ""
		}

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select scac_cde from b2b_cde_conversion where convert_type_id = 'CS1CarrierID' and ext_cde = ?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(util.getDBRowLimit());
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, id);
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
	

	public void generateBody(Body current_Body, MarkupBuilder outXml,First_Body) {

		//CT special fields
		def vCS1Event = current_Body?.Event?.CS1Event?.trim()

		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)

		LocDT EventDateTime = current_Body?.Event?.EventDT?.LocDT
		def vCS1EventCodeConversion = util.getConversion('KUBOTAYUSEN', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)

		def shipDir = util.getEDICdeReffromIntCde('KUBOTAYUSEN','ShipDirection','O','INTCTXML','Q2','EventStatusCode',vCS1Event,conn)

		OceanLeg firstOceanLeg = null

		OceanLeg lastOceanLeg = null

		def Ocean =current_Body?.Route?.OceanLeg
		String id = util.getCarrierID(First_Body?.GeneralInformation?.SCAC,conn).trim()
		String BL_SCAC = getScac_cde(id,conn)

		if(Ocean){
			firstOceanLeg = current_Body?.Route?.OceanLeg[0]
			lastOceanLeg = current_Body?.Route?.OceanLeg[-1]
		}

		BookingGeneralInfo firstBookingGeneralInfo = null
		BookingGeneralInfo lastBookingGeneralInfo = null

		def bookingGeneralInfo =  current_Body?.BookingGeneralInfo
		def default_E159_06=null

		if(bookingGeneralInfo){
			firstBookingGeneralInfo = current_Body?.BookingGeneralInfo[0]
			lastBookingGeneralInfo = current_Body?.BookingGeneralInfo[-1]
		}

		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '-999'
			}

			'B4' {
				'E157_03' vCS1EventCodeConversion

				'E373_04' util.convertDateTime(EventDateTime, xmlDateTimeFormat, yyyyMMdd)

				'E161_05' util.convertDateTime(EventDateTime, xmlDateTimeFormat, HHmm)

				//06-159 Type1
				//				if  (current_Body.Event?.Location?.LocationCode?.UNLocationCode)
				//				{
				//					'E159_06' current_Body.Event?.Location?.LocationCode?.UNLocationCode
				//				}
				//				else  if (current_Body.Event?.Location?.LocationCode?.SchedKDCode ) {
				//					'E159_06' current_Body.Event?.Location?.LocationCode?.SchedKDCode
				//				}
				//				else
				//				{
				//					'E159_06' ''
				//				}

				//06-159, Type6
				if(current_Body.Event?.Location?.LocationCode?.SchedKDCode)
				{
					'E159_06' current_Body.Event?.Location?.LocationCode?.SchedKDCode
				}
				else if  (current_Body.Event?.Location?.LocationCode?.UNLocationCode)
				{
					'E159_06' current_Body.Event?.Location?.LocationCode?.UNLocationCode
				}
				else
				{
					'E159_06' ''
				}

				if(current_Body.Container?.ContainerNumber?.length()>0){
					'E206_07' util.substring(current_Body?.Container?.ContainerNumber, 1, 4)
				}

				if(current_Body?.Container?.ContainerNumber?.length()>=5){
					'E207_08' util.substring(current_Body?.Container?.ContainerNumber,5,6)
				}

				if(current_Body?.Container?.ContainerStatus =='Empty'){
					'E578_09' 'E'
				}else if(current_Body?.Container?.ContainerStatus =='Laden'){
					'E578_09' 'L'
				}else if(!current_Body?.Container?.ContainerStatus && ['CS010', 'CS210'].contains(vCS1Event)){
					'E578_09' 'E'
				}else if(!current_Body?.Container?.ContainerStatus && !['CS010', 'CS210'].contains(vCS1Event)){
					'E578_09' 'L'
				}

				'E761_13'  util.substring(current_Body?.Container?.ContainerCheckDigit, 1,2)
			}

			//Loop N9
			//for avoid duplicated N9

			def SNs = []
			int N9Count=0   //N9_LIMIT_30
			//def N9list = [] 

			//BLNumber
			current_Body?.BLGeneralInfo?.findAll{StringUtil.isNotEmpty(it.BLNumber)}?.groupBy{it?.BLNumber?.trim()}?.each { BLNumber, current_BLGeneralInfo ->
				if (N9Count<30){
					'N9' {
						'E128_01' 'BM'
						if(BLNumber.toString().startsWith(BL_SCAC) || BL_SCAC =='' || BL_SCAC == null){
							'E127_02' util.substring(BLNumber,1,30)
						}else
							'E127_02'  util.substring((BL_SCAC + BLNumber),1,30)
					}
					N9Count++
				}
			}

			//BookingNumber
			current_Body?.BookingGeneralInfo?.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}?.groupBy{it?.CarrierBookingNumber?.trim()}?.each { BookingNumber, current_BookingGeneralInfoGroup ->
				if (N9Count<30){
					'N9' {
						'E128_01' 'BN'
						'E127_02' util.substring(BookingNumber,1,30)
					}
					N9Count++
				}
			}
			//seal number
			current_Body?.Container?.Seal?.findAll{StringUtil.isNotEmpty(it?.SealNumber) && it?.SealNumber?.length() >= 4}?.groupBy{it?.SealNumber?.trim()}?.each { sealNumber, sealGroup ->
				if(N9Count<30 && !SNs.contains(util.substring(sealNumber,1,12))){
					SNs.add(util.substring(sealNumber,1,12))
					'N9' {
						'E128_01' 'SN'
						'E127_02' util.substring(sealNumber,1,12)
					}
					N9Count++
				}
			}

			//FalicityCode
			String Facility_IntCde=current_Body?.Route?.LastPOD?.Facility?.FacilityCode
			String Facility_ExtCde = util.getConversionWithoutTP('CT','O','FacilityCode',Facility_IntCde, conn)
			LocDT firstEstDepartureDate = firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT
			LocDT firstActDepartureDate = firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT
			if(N9Count<30 && Facility_ExtCde){
				if((EventDateTime && firstActDepartureDate &&
				(util.convertDateTime(EventDateTime,xmlDateTimeFormat,"yyyy-MM-dd HH:mm:ss")>=
				util.convertDateTime(firstActDepartureDate,xmlDateTimeFormat,"yyyy-MM-dd HH:mm:ss")))
				||(EventDateTime && firstEstDepartureDate &&
				(util.convertDateTime(EventDateTime,xmlDateTimeFormat,"yyyy-MM-dd HH:mm:ss")>=
				util.convertDateTime(firstEstDepartureDate,xmlDateTimeFormat,"yyyy-MM-dd HH:mm:ss")))){
					'N9'{
						'E128_01' 'LU'
						'E127_02' Facility_ExtCde
					}
					N9Count++
				}
			}

			def CSSTDQ2ExtCDE=ctUtil.getCodeConversionbyCovertType('CT','O','CSSTDQ2',vCS1EventFirst5,conn)
			if(CSSTDQ2ExtCDE && N9Count != 0) {
				if (shipDir == 'I') {
					'Q2'{

						if(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber?.length()>0){
							'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber,1,7)
						}

						if(lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode?.length()>0){
							'E26_02' util.substring(lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode,1,8)
						}

						if(lastOceanLeg?.SVVD?.Discharge?.Voyage || lastOceanLeg?.SVVD?.Discharge?.Direction){
							'E55_09' util.substring((lastOceanLeg?.SVVD?.Discharge?.Voyage ? lastOceanLeg?.SVVD?.Discharge?.Voyage : "") +
									(lastOceanLeg?.SVVD?.Discharge?.Direction ? lastOceanLeg?.SVVD?.Discharge?.Direction : ""),1,10)
						}
						'E897_12' 'L'

						if(lastOceanLeg?.SVVD?.Discharge?.VesselName) {
							'E182_13' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName, 1, 28)
						}

					}
				} else if (shipDir == 'O') {

					'Q2'{
						if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber?.length()>0){
							'E597_01' util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber,1,7)
						}

						if(firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode?.length()>0){
							'E26_02' util.substring(firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode,1,8)
						}

						if(firstOceanLeg?.SVVD?.Loading?.Voyage || firstOceanLeg?.SVVD?.Loading?.Direction){
							'E55_09' util.substring((firstOceanLeg?.SVVD?.Loading?.Voyage ? firstOceanLeg?.SVVD?.Loading?.Voyage : "") + (firstOceanLeg?.SVVD?.Loading?.Direction ? firstOceanLeg?.SVVD?.Loading?.Direction : ""),1,10)
						}

						'E897_12' 'L'

						if(firstOceanLeg?.SVVD?.Loading?.VesselName) {
							'E182_13' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName, 1, 28)
						}
					}
				}
			}

			//R4

			POR POR =  current_Body?.Route?.POR

			FirstPOL firstPOL =  current_Body?.Route?.FirstPOL

			LastPOD lastPOD =  current_Body?.Route?.LastPOD

			FND FND = current_Body?.Route?.FND

			//POR
			// allow msg to be completed when POR UNLocde or SKDcode missing
			'Loop_R4' {
				'R4' {
					'E115_01' 'R'
					if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					}

					if(POR?.CityDetails?.LocationCode?.UNLocationCode) {
						'E310_03' POR.CityDetails.LocationCode.UNLocationCode
					}


					if (util.isNotEmpty(POR?.CityDetails?.City)) {
						'E114_04' util.substring(POR?.CityDetails?.City,1,24)
					}

					if (POR?.CSStandardCity?.CSCountryCode){
						'E26_05' POR?.CSStandardCity?.CSCountryCode
					}

					if (POR?.CSStandardCity?.CSStateCode) {
						'E156_08'  util.substring(POR?.CSStandardCity?.CSStateCode,1,2)
					}
				}

				LocDT porDTM = null
				def isAct = false
				if (current_Body?.Route?.CargoReceiptDT?.find{it?.attr_Indicator == 'A'}?.LocDT) {
					porDTM = current_Body?.Route?.CargoReceiptDT?.find{it?.attr_Indicator== 'A'}?.LocDT
					isAct = true
				} else{
					if (current_Body?.Route?.FullPickupDT?.find{it?.attr_Indicator == 'E'}?.LocDT && firstBookingGeneralInfo?.Haulage?.OutBound == 'C') {
						porDTM = current_Body?.Route?.FullPickupDT?.find{it?.attr_Indicator == 'E'}?.LocDT
					}else if (current_Body?.Route?.FullReturnCutoffDT?.find{it?.attr_Indicator == 'A'}?.LocDT && firstBookingGeneralInfo?.Haulage?.OutBound == 'M') {
						porDTM = current_Body?.Route?.FullReturnCutoffDT?.find{it?.attr_Indicator == 'A'}?.LocDT
					}
				}
				if(porDTM){
					'DTM' {
						if(isAct){
							'E374_01' '140'
						}else{
							'E374_01' '139'
						}
						'E373_02' util.convertDateTime(porDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(porDTM, xmlDateTimeFormat, HHmm)
					}
				}
			}

			//LastPOD
			'Loop_R4' {
				'R4' {
					'E115_01' 'D'

					if(lastPOD?.Port?.LocationCode?.UNLocationCode){
						'E309_02' 'UN'
					}


					if(lastPOD?.Port?.LocationCode?.UNLocationCode){
						'E310_03' lastPOD.Port.LocationCode.UNLocationCode.trim()
					}


					if (util.isNotEmpty(lastPOD?.Port?.PortName)) {
						'E114_04' util.substring(lastPOD?.Port?.PortName,1,24)
					}else if(util.isNotEmpty(lastPOD?.Port?.City)){
						'E114_04' util.substring(lastPOD?.Port?.City,1,24)
					}


					if (lastPOD?.Port?.CSCountryCode){
						'E26_05' lastPOD?.Port?.CSCountryCode
					}

					if (lastPOD?.CSStateCode) {
						'E156_08' util.substring(lastPOD?.CSStateCode,1,2)
					}
				}

				LocDT lastPODDTM = null
				def isAct = false
				if(lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT){
					lastPODDTM = lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
					isAct = true
				}else if(lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT){
					lastPODDTM =lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
				}

				if(lastPODDTM){
					'DTM' {
						if(isAct){
							'E374_01' '140'
						}else{
							'E374_01' '139'
						}
						'E373_02' util.convertDateTime(lastPODDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(lastPODDTM, xmlDateTimeFormat, HHmm)
					}
				}
			}

			//FirstPOL
			'Loop_R4' {
				'R4' {
					'E115_01' 'L'
					if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					}

					if(firstPOL?.Port?.LocationCode?.UNLocationCode){
						'E310_03' firstPOL?.Port?.LocationCode?.UNLocationCode
					}


					if(util.isNotEmpty(firstPOL?.Port?.PortName)){
						'E114_04' util.substring(firstPOL?.Port?.PortName,1,24)
					}else if(util.isNotEmpty(firstPOL?.Port?.City)){
						'E114_04' util.substring(firstPOL?.Port?.City,1,24)
					}


					if(firstPOL?.Port?.CSCountryCode){
						'E26_05' firstPOL?.Port?.CSCountryCode
					}

					if (firstPOL?.CSStateCode) {
						'E156_08' util.substring(firstPOL?.CSStateCode,1,2)
					}
				}
				LocDT firstPOLDTM = null
				def isAct = false
				if(firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT){
					firstPOLDTM = firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'A'}?.LocDT
					isAct = true
				}else if(firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT){
					firstPOLDTM =firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator == 'E'}?.LocDT
				}

				if(firstPOLDTM){
					'DTM' {
						if(isAct){
							'E374_01' '140'
						}else{
							'E374_01' '139'
						}
						'E373_02' util.convertDateTime(firstPOLDTM, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(firstPOLDTM, xmlDateTimeFormat, HHmm)
					}
				}

			}

			//FND
			// allow msg to be completed when FND UNLocde or SKDcode missing
			'Loop_R4' {
				'R4' {
					'E115_01' 'E'

					if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
						'E309_02' 'UN'
					}


					if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
						'E310_03' FND.CityDetails.LocationCode.UNLocationCode.trim()
					}


					if (util.isNotEmpty(FND?.CityDetails?.City)) {
						'E114_04' util.substring(FND?.CityDetails?.City,1,24)
					}

					if (FND?.CSStandardCity?.CSCountryCode){
						'E26_05' FND?.CSStandardCity?.CSCountryCode
					}
					if (FND?.CSStandardCity?.CSStateCode) {
						'E156_08' util.substring(FND?.CSStandardCity?.CSStateCode,1,2)
					}
				}

				LocDT fndDatetime = null
				def fndisAct = false

				if(current_Body?.Route?.CargoDeliveryDT?.find{it?.attr_Indicator=='A'}?.LocDT){
					fndDatetime = current_Body?.Route?.CargoDeliveryDT?.find{it?.attr_Indicator=='A'}?.LocDT
					fndisAct  =true
				}else if(firstBookingGeneralInfo?.Haulage?.InBound =='M'){
					if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator == 'E' }?.LocDT){
						fndDatetime = current_Body?.Route?.ArrivalAtFinalHub?.find { it?.attr_Indicator == 'E' }?.LocDT
						fndisAct = false
					}else if(current_Body?.Route?.FND?.ArrivalDT?.find{it?.attr_Indicator == 'E' }?.LocDT && current_Body?.GeneralInformation?.SCAC!='OOLU'){
						fndDatetime = current_Body?.Route?.FND?.ArrivalDT?.find{ it?.attr_Indicator == 'E' }?.LocDT
						fndisAct = false
					}
				}else if(firstBookingGeneralInfo?.Haulage?.InBound =='C'){
					if(current_Body?.Route?.CargoDeliveryDT?.find{it?.attr_Indicator == 'E' }?.LocDT){
						fndDatetime = current_Body?.Route?.CargoDeliveryDT?.find { it?.attr_Indicator == 'E' }?.LocDT
						fndisAct = false
					}
				}


				if (fndDatetime) {
					'DTM' {
						if (fndisAct) {
							'E374_01' '140'
						} else {
							'E374_01' '139'
						}
						'E373_02' util.convertDateTime(fndDatetime, xmlDateTimeFormat, yyyyMMdd)
						'E337_03' util.convertDateTime(fndDatetime, xmlDateTimeFormat, HHmm)
					}
				}
			}

			//Type1, V9
			LocDT V9_lastOceanleg_POD = null
			LocDT V9_firstOceanleg_POL = null
			LocDT V9_FND_DateTime = null

			if(lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT){
				V9_lastOceanleg_POD = lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='A'}?.LocDT
			} else if(lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT){
				V9_lastOceanleg_POD = lastOceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT
			}

			if(V9_lastOceanleg_POD){
				'V9' {
					'E304_01'   'EAD'
					'E106_02'   'PORT OF DISCHARGE'
					'E373_03'   util.convertDateTime(V9_lastOceanleg_POD, xmlDateTimeFormat, yyyyMMdd)
					'E337_04'   util.convertDateTime(V9_lastOceanleg_POD, xmlDateTimeFormat, HHmm)
				}
			}

			if(firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='A'}?.LocDT){
				V9_firstOceanleg_POL = firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='A'}?.LocDT

			}else if(firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='E'}?.LocDT){
				V9_firstOceanleg_POL = firstOceanLeg?.POL?.DepartureDT?.find{it?.attr_Indicator=='E'}?.LocDT
			}

			if(V9_firstOceanleg_POL){
				'V9' {
					'E304_01'   'EDD'
					'E106_02'   'PORT OF LOAD'
					'E373_03'   util.convertDateTime(V9_firstOceanleg_POL, xmlDateTimeFormat, yyyyMMdd)
					'E337_04'   util.convertDateTime(V9_firstOceanleg_POL, xmlDateTimeFormat, HHmm)
				}
			}
			if(current_Body?.Route?.CargoDeliveryDT?.find{it?.attr_Indicator=='A'}?.LocDT){
				V9_FND_DateTime = current_Body?.Route?.CargoDeliveryDT?.find{it?.attr_Indicator=='A'}?.LocDT
			}else if(util.substring(lastBookingGeneralInfo?.Haulage?.InBound,1,1)=='M'){
				if(current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator=='E'}?.LocDT){
					V9_FND_DateTime = current_Body?.Route?.ArrivalAtFinalHub?.find{it?.attr_Indicator=='E'}?.LocDT
				}else if((current_Body?.Route?.FND?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT )&& current_Body?.GeneralInformation?.SCAC!='OOLU'){
					V9_FND_DateTime = current_Body?.Route?.FND?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT
				}
			} else if(util.substring(lastBookingGeneralInfo?.Haulage?.InBound,1,1)=='C'&&current_Body?.Route?.CargoDeliveryDT?.find{it?.attr_Indicator=='E'}?.LocDT){
				V9_FND_DateTime = current_Body?.Route?.CargoDeliveryDT?.find{it?.attr_Indicator=='E'}?.LocDT
			}
			if(V9_FND_DateTime){
				'V9' {
					'E304_01'   'ERD'
					'E106_02'   'FINAL DESTINATION'
					'E373_03'   util.convertDateTime(V9_FND_DateTime, xmlDateTimeFormat, yyyyMMdd)
					'E337_04'   util.convertDateTime(V9_FND_DateTime, xmlDateTimeFormat, HHmm)
				}
			}

			'SE' {
				//SE-01 is auto line counter by BelugaOcean, so here need place a space is ok
				'E96_01' '-999'
				'E329_02' '-999'
			}
		}
	}

	String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {

		/**
		 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
		 */
		//inputXmlBody = util.removeBOM(inputXmlBody)

		/**
		 * Part II: get OLL mapping runtime parameters
		 */
		//[]{"B2BSessionID="+B2BSessionID, "B2B_OriginalSourceFileName="+B2B_OriginalSourceFileName, "B2B_SendPortID="+B2B_SendPortID, "PortProperty="+PortProperty, "MSG_REQ_ID="+MSG_REQ_ID, "TP_ID="+TP_ID, "MSG_TYPE_ID="+MSG_TYPE_ID, "DIR_ID="+DIR_ID};
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
		//Important: the inputXml is xml root element
		//		def parser = new XmlParser()
		//		parser.setNamespaceAware(false);
		//		def ContainerMovement = parser.parseText(inputXmlBody);

		XmlBeanParser parser = new XmlBeanParser()
		ContainerMovement ct = parser.xmlParser(inputXmlBody, ContainerMovement.class)

		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		def bizKeyWriter = new StringWriter();
		def bizKeyXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(bizKeyWriter), "", false));
		bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

		def csuploadWriter = new StringWriter();
		def csuploadXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(csuploadWriter), "", false));
		csuploadXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")


		/**
		 * Part IV: mapping script start from here
		 */

		//create root node
		def T315 = outXml.createNode('T315')
		def bizKeyRoot = bizKeyXml.createNode('root')
		def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.


		//BeginWorkFlow
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		def outputBodyCount = 0
		currentSystemDt = new Date()
		//def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, "X.12", conn)
		def headerMsgDT = util.convertDateTime(ct?.Header?.MsgDT?.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		//long ediIsaCtrlNumber = ctrlNos[0]
		//long ediGroupCtrlNum = ctrlNos[1]
		def txnErrorKeys = []
		//		generateEDIHeader(outXml, appSessionId, ct, currentSystemDt)

		//duplication -- CT special logic
		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)

		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

		//start body looping
		bodies.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body?.Event?.CS1Event?.trim()
			def eventExtCde = util.getConversion('KUBOTAYUSEN', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex, eventCS2Cde, eventExtCde)

			if (errorKeyList.size() == 0) {
				//pass validateBeforeExecution
				//main mapping
				generateBody(current_Body, outXml,bodies[0])
				outputBodyCount++
			}

			//posp checking
			pospValidation()

			ctUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
			ctUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, eventExtCde, TP_ID, conn)

			txnErrorKeys.add(errorKeyList);
		}
		//		generateEDITail(outXml, outputBodyCount)
		//EndWorkFlow

		//End root node
		outXml.nodeCompleted(null,T315)
		bizKeyXml.nodeCompleted(null,bizKeyRoot)
		csuploadXml.nodeCompleted(null,csuploadRoot)

		//		println bizKeyWriter.toString();
		//		println csuploadWriter.toString();

		//promote csupload and bizkey to session
		ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		ctUtil.promoteAssoInterchangeMessageIDToSession(appSessionId,ct?.Header?.InterchangeMessageID);
		if(ct.Body[0]?.GeneralInformation?.SCAC)ctUtil.promoteAssoCarrierSCACToSession(appSessionId,ct.Body[0]?.GeneralInformation?.SCAC);

		String result = "";
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			//if exists one txn without error, then return result
			result = util.cleanXml(writer?.toString())
		}

		writer.close();
		csuploadWriter.close()
		bizKeyWriter.close()

		return result;
	}
	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();

		//		def Location=current_Body.Event.Location
		//		def CityDetails=current_Body.Event.Location.CityDetails

		String containsCustomerCode='no'
		//		def Legal_IntCde=['CGN','CCP','FWD','NPT','SHP']
		def CustomerCode=false
		if(current_Body.Party.findAll{it.PartyLevel!='BL'&&it.PartyLevel!='BK'&&(it.PartyType!='SH'||it.PartyType!='FW'||it.PartyType!='CN')&&CustomerCode&&containsCustomerCode=='no'}){
			containsCustomerCode='yes'
		}else{
			containsCustomerCode='no'
		}

		def eventCityName = current_Body?.Event?.Location?.CityDetails?.City
		def eventStateCode = util.substring(current_Body?.Event?.Location?.CityDetails?.State?.toUpperCase(),1,2)
		def eventCountryCode = current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode


		//error checking
		ctUtil.missingEventStatusCode(eventCS2Cde,eventExtCde,true, null, errorKeyList)
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body?.Event?.EventDT?.LocDT, true, null, errorKeyList)
		//DestinationCountryCodenotUS(eventCS2Cde,current_Body?.Route?.LastPOD,true,null,errorKeyList)
		String missingEventStatusLocationMsg = ' - Missing Event Status Location for '+
				(eventCityName ? eventCityName :"") + "/" + (eventStateCode ? eventStateCode : "")+"/"+
				(eventCountryCode ? eventCountryCode : "")

		//missingEventStatusLocation(eventCS2Cde,current_Body?.Event?.Location?.LocationCode?.UNLocationCode,
		//	current_Body?.Event?.Location?.LocationCode?.SchedKDCode,true,missingEventStatusLocationMsg,errorKeyList)

		//ctUtil.missingEventStatusLocation(eventCS2Cde,current_Body?.Event,true,missingEventStatusLocationMsg,errorKeyList)

		//ctUtil.missingContainerNumber(eventCS2Cde,current_Body?.Container,false,"- Missing Equipment Number",errorKeyList)

//				if(current_Body?.BookingGeneralInfo.size()>0){
//					ctUtil.missingBookingNumber(eventCS2Cde,current_Body?.BookingGeneralInfo,false,null,errorKeyList)
//				}

		//ctUtil.missingBookingNumber(eventCS2Cde,current_Body?.BookingGeneralInfo,true,null,errorKeyList)
		//missingCarrierBookingNumber(eventCS2Cde,current_Body?.BookingGeneralInfo,true,null,errorKeyList)


		def routeFilter = (util.isNotEmpty(current_Body?.Route?.Outbound_intermodal_indicator) ? current_Body?.Route?.Outbound_intermodal_indicator : '0') + (util.isNotEmpty(current_Body?.Route?.Inbound_intermodal_indicator) ? current_Body?.Route?.Inbound_intermodal_indicator : '0') + (util.isNotEmpty(current_Body?.Route?.FND?.Facility?.FacilityCode) ? current_Body?.Route?.FND?.Facility?.FacilityCode : '')

		ctUtil.filterIBIntermodal(eventCS2Cde,'CS260',routeFilter[1],false,null,errorKeyList)


		ctUtil.EventIsNotSubscribedByParnter(eventCS2Cde,eventExtCde,false,null,errorKeyList)
		EventNotSub(eventCS2Cde,eventExtCde,true,eventCS2Cde,errorKeyList)



		//		AllowCS260Intermodal(eventCS2Cde,current_Body?.Route?.Outbound_intermodal_indicator,current_Body?.Route?.Inbound_intermodal_indicator,current_Body?.Route?.FND?.Facility?.FacilityCode,false,null,errorKeyList)
		//B403,type3
		//CS160NotInTruck(eventCS2Cde,current_Body?.GeneralInformation?.TransportMode,false,null,errorKeyList)
		ctUtil.filterEventCodeNotinTruck(eventCS2Cde,'CS160',current_Body?.GeneralInformation?.TransportMode,false,null,errorKeyList)


		// error cases

		/*
		 ctUtil.missingEventStatusCode2(eventCS2Cde, eventExtCde, true, null, errorKeyList)
		 ctUtil.missingEventStatusDate1(eventCS2Cde, current_Body.Event.EventDT.LocDT, true, null, errorKeyList)
		 ctUtil.missEventLocationCode(eventExtCde,Location,CityDetails,true, null, errorKeyList)
		 ctUtil.missEquipmentNumber2(eventCS2Cde, current_Body.Container, true, null, errorKeyList)
		 ctUtil.missBookingNumber(eventCS2Cde, current_Body.BookingGeneralInfo, true, null, errorKeyList)
		 */
		//		ctUtil.missingCustomerCode(eventCS2Cde,containsCustomerCode,true, null, errorKeyList)  // occure error

		// obsolete cases


		// obsolete cases
		//	ctUtil.missingBlNumber2(eventCS2Cde, current_Body.BLGeneralInfo, false, null, errorKeyList)

		//CS060
		missingR4_03_310(current_Body?.Route?.LastPOD?.Port?.LocationCode?.UNLocationCode,true,eventCS2Cde+' - Missing Body.Route.LastPOD.Port.LocationCode.UNLocationCode.',errorKeyList)
		missingR4_03_310(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode,true,eventCS2Cde+' - Missing Body.Route.FirstPOL.Port.LocationCode.UNLocationCode.',errorKeyList)
		missingR4_03_310(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode,true,eventCS2Cde+' - Missing Body.Route.FND.CityDetails.LocationCode.UNLocationCode',errorKeyList)
		missingR4_03_310(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode,true,eventCS2Cde+' - Missing Body.Route.POR.CityDetails.LocationCode.UNLocationCode',errorKeyList)

		return errorKeyList;
	}

	private static final String YES = 'YES'
	private static final String NO = 'NO'
	private static final String C = 'C'
	private static final String ERROR_SUPPORT = 'ES'
	private static final String ERROR_COMPLETE = 'EC'

	//CSUPLOAD
	//
	public static void missingR4_03_310(String eventUNLocationCode,boolean isError,String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null

		if(StringUtil.isEmpty(eventUNLocationCode)){
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg]
			errorKeyList.add(errorKey)
		}
	}
	
	
	public static void EventNotSub(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg, List<Map<String, String>> errorKeyList) {
		Map<String, String> errorKey = null
		if (StringUtil.isEmpty(eventExtCde)) {
			errorKey = [TYPE: isError ? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError ? YES : NO, VALUE: errorMsg != null ? eventCS2Cde + errorMsg : eventCS2Cde + ' - EVT_NOT_SUB']
			errorKeyList.add(errorKey)
		}
	}
	//	public static void missingCarrierBookingNumber(String eventCS2Cde, List<cs.b2b.core.mapping.bean.ct.BookingGeneralInfo> bookingGeneralInfo,boolean isError,String errorMsg, List<Map<String,String>> errorKeyList){
	//		Map<String,String> errorKey = null
	//		if(bookingGeneralInfo.size()>0){
	//			if (bookingGeneralInfo.findAll{StringUtil.isNotEmpty(it?.CarrierBookingNumber)}.size() == 0) {
	//				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE:errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Booking Number']
	//				errorKeyList.add(errorKey);
	//			}
	//		}
	//
	//	}

	//Destination Country Code is not US

	//	public static void DestinationCountryCodenotUS(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.LastPOD lastPOD,boolean isError,String errorMsg, List<Map<String,String>> errorKeyList){
	//		Map<String,String> errorKey = null
	//		if(lastPOD.Port.CSCountryCode!="US"){
	//			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : 'Destination Country Code is not US']
	//
	//			errorKeyList.add(errorKey)
	//		}
	//	}


	//"CS160_NOT_IN_TRUCK"
	//	public static void CS160NotInTruck(String eventCS2Cde,String TransportMode,boolean isError,String errorMsg, List<Map<String,String>> errorKeyList){
	//		Map<String,String> errorKey = null
	//		if(eventCS2Cde=='CS160' && TransportMode?.toUpperCase()!='TRUCK'){
	//			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '  not in transport mode (TRUCK).']
	//				errorKeyList.add(errorKey)
	//		}
	//
	//	}
	// ALLOW_CS260_INTERMODAL
	//	public static void AllowCS260Intermodal(String eventCS2Cde,String OBIntermodalInd,String IBIntermodalInd,String FNDFacilityCode,boolean isError,String errorMsg, List<Map<String,String>> errorKeyList){
	//		Map<String,String> errorKey = null
	//		def routeFilter = (OBIntermodalInd ? OBIntermodalInd : '0') + (IBIntermodalInd ? IBIntermodalInd : '0') + (FNDFacilityCode ? FNDFacilityCode : '')
	//		if(eventCS2Cde=='CS260' && routeFilter[1]=='1'){
	//			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '- Ignore CS260 (I/B intermodal)']
	//			errorKeyList.add(errorKey)
	//		}
	//
	//
	//	}

	public boolean pospValidation() {

	}

	//		private  void generateEDIHeader(MarkupBuilder outXml, def appSessionId, ContainerMovement ContainerMovement, def currentSystemDt) {
	//			cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', ContainerMovement.Header.InterchangeMessageID);
	//			cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', ContainerMovement.Body[0]?.GeneralInformation?.SCAC);
	//
	//			/*
	//			outXml.'ISA' {
	//				'I01_01' '00'
	//				'I02_02' '          '
	//				'I03_03' '00'
	//				'I04_04' '          '
	//				'I05_05' 'ZZ'
	//				'I06_06' 'CARGOSMART     '
	//				'I05_07' '12'
	//				'I07_08' '2013308849     '
	//				'I08_09' currentSystemDt.format("yyMMdd")
	//				'I09_10' currentSystemDt.format(HHmm)
	//				'I10_11' 'U'
	//				'I11_12' '00401'
	//				'I12_13' '###edi_Isa_Ctrl_Number###'
	//				'I13_14' '0'
	//				'I14_15' 'P'
	//				'I15_16' '>'
	//			}
	//			outXml.'GS' {
	//				'E479_01' 'QO'
	//				'E142_02' 'CARGOSMART'
	//				'E124_03' 'JCP'
	//				'E373_04' currentSystemDt.format(yyyyMMdd)
	//				'E337_05' currentSystemDt.format(HHmm)
	//				'E28_06' '###edi_Group_Ctrl_Number###'
	//				'E455_07' 'X'
	//				'E480_08' '004010'
	//			}
	//			*/
	//
	//		}
	//
	//		private void generateEDITail(MarkupBuilder outXml, def outputBodyCount) {
	//
	//			/*
	//			outXml.'GE' {
	//				'E97_01' outputBodyCount
	//				'E28_02' '###edi_Group_Ctrl_Number###'
	//			}
	//			outXml.'IEA' {
	//				'I16_01' '1'
	//				'I12_02' '###edi_Isa_Ctrl_Number###'
	//			}
	//			*/
	//		}

}