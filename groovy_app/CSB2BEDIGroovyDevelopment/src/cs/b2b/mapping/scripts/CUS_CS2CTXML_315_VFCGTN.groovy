package cs.b2b.mapping.scripts

import cs.b2b.core.mapping.bean.edi.xml.br.CSSTD.LegalParties
import groovy.xml.MarkupBuilder

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.List
import java.util.Map

import cs.b2b.beluga.common.tools.DefinitionGeneratorFromBiztalkSchema;
import cs.b2b.core.common.util.StringUtil;
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.Body
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

public class CUS_CS2CTXML_315_VFCGTN{

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
	public String getConversionWithScacAndMsg_Fmt_Id(String TP_ID, String Msg_Fmt_Id, String DIR_ID, String convertTypeId, String fromValue, String SCAC, Connection conn) throws Exception {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "select ext_cde from b2b_cde_conversion where tp_id=? and msg_fmt_id=? and dir_id=? and convert_type_id=? and int_cde=? and scac_cde=?";

		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(util.getDBRowLimit());
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, TP_ID);
			pre.setString(2, Msg_Fmt_Id);
			pre.setString(3, DIR_ID);
			pre.setString(4, convertTypeId);
			pre.setString(5, fromValue);
			pre.setString(6, SCAC);
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
	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		//CT special fields
		def vCS1Event = current_Body.Event.CS1Event

		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)

		def vCS1EventCodeConversion = util.getConversion('VFCGTN', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)

		def shipDir =  util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.

		def Int_Cde=current_Body.Container?.CarrCntrSizeType

		def vCS2EventCodeConversion=getConversionWithScacAndMsg_Fmt_Id('GTNEXUS','X.12','O','ContainerType',Int_Cde,'OOLU',conn)

		OceanLeg firstOceanLeg = null

		OceanLeg lastOceanLeg = null

		POR POR =  current_Body.Route.POR

		FirstPOL firstPOL =  current_Body.Route.FirstPOL

		LastPOD lastPOD =  current_Body.Route.LastPOD

		FND FND =  current_Body.Route?.FND

		def EventLocationName=current_Body.Event?.Location?.LocationName

		def EventCityName =current_Body.Event?.Location?.CityDetails?.City

		def EventLocationCodeUN= current_Body.Event?.Location?.LocationCode?.UNLocationCode

		def EventSchedKDCode=current_Body.Event?.Location?.LocationCode?.SchedKDCode

		def Ocean =current_Body.Route.OceanLeg

		def Record_VESSEL_VOYAGEREC=Ocean.findAll{it.POD?.Port?.PortName==EventLocationName&&it.POD?.Port?.City==EventCityName&&it.POD?.Port?.LocationCode?.UNLocationCode==EventLocationCodeUN}

		def Record_VESSEL_VOYAGEREC1 = Ocean.findAll{it.POL?.Port?.PortName==EventLocationName&&it.POL?.Port?.City==EventCityName&&it.POL?.Port?.LocationCode?.UNLocationCode==EventLocationCodeUN}

		if(Ocean){
			firstOceanLeg = current_Body.Route.OceanLeg[0]
			lastOceanLeg = current_Body.Route.OceanLeg[-1]
		}
		def POL_E310_03=null

		def FND_E310_03=null

		def POD_E310_03=null

		def default_E159_06=null

		if (EventLocationCodeUN){
			default_E159_06 = EventLocationCodeUN
		}else if( EventSchedKDCode){
			default_E159_06= EventSchedKDCode
		}else{
			default_E159_06= '9999'
		}

		if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
			POD_E310_03= lastPOD?.Port?.LocationCode?.UNLocationCode
		} else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
			POD_E310_03= lastPOD?.Port?.LocationCode?.SchedKDCode
		}else if(!lastPOD?.Port?.LocationCode?.UNLocationCode&&!lastPOD?.Port?.LocationCode?.SchedKDCode&&
		((['CS120','CS130','CS160','CS190','CS080'].contains(vCS1EventFirst5))||((['CS140','CS260','CS220'].contains(vCS1EventFirst5))&&((lastPOD?.Port?.PortName==
		current_Body.Event?.Location?.LocationName&&lastPOD?.Port?.PortName)||(current_Body.Event?.Location?.CityDetails?.City==lastPOD?.Port?.City&&lastPOD?.Port?.City)))))
		{
			POD_E310_03= '9999'
		}else if(!lastPOD?.Port?.LocationCode?.UNLocationCode&&!lastPOD?.Port?.LocationCode?.SchedKDCode&&
		((!['CS120','CS130','CS160','CS190','CS080'].contains(vCS1EventFirst5))&&((!['CS140','CS260','CS220'].contains(vCS1EventFirst5))||((lastPOD?.Port?.PortName!=
		current_Body.Event?.Location?.LocationName||!lastPOD?.Port?.PortName)&&(current_Body.Event?.Location?.CityDetails?.City!=lastPOD?.Port?.City||!lastPOD?.Port?.City)))))
		{
			POD_E310_03= 'XXXX'
		}

		if (firstPOL?.Port?.LocationCode?.UNLocationCode)
		{
			POL_E310_03= firstPOL?.Port?.LocationCode?.UNLocationCode
		}else if (firstPOL?.Port?.LocationCode?.SchedKDCode)
		{
			POL_E310_03= firstPOL?.Port?.LocationCode?.SchedKDCode
		}else if (!firstPOL?.Port?.LocationCode?.UNLocationCode&&!firstPOL?.Port?.LocationCode?.SchedKDCode&&
		(['CS030','CS040','CS060','CS070','CS110','CS047'].contains(vCS1EventFirst5))){
			POL_E310_03= '9999'
		}
		else if (!firstPOL?.Port?.LocationCode?.UNLocationCode&&!firstPOL?.Port?.LocationCode?.SchedKDCode&&
		(!['CS030','CS040','CS060','CS070','CS110','CS047'].contains(vCS1EventFirst5))){
			POL_E310_03= 'XXXX'
		}

		if(FND?.CityDetails?.LocationCode?.UNLocationCode) {
			FND_E310_03= FND?.CityDetails?.LocationCode?.UNLocationCode
		} else if (FND.CityDetails?.LocationCode?.SchedKDCode) {
			FND_E310_03= FND.CityDetails.LocationCode.SchedKDCode
		}else if(!FND?.CityDetails?.LocationCode?.UNLocationCode&&!FND.CityDetails?.LocationCode?.SchedKDCode&&
		(['CS200','CS220','CS2140','CS260'].contains(vCS1EventFirst5)&&(FND?.CityDetails?.City==current_Body.Event?.Location?.CityDetails?.City&&FND?.CityDetails?.City)))
		{
			FND_E310_03= '9999'
		}else if(!FND?.CityDetails?.LocationCode?.UNLocationCode&&!FND.CityDetails?.LocationCode?.SchedKDCode&&
		(vCS1EventFirst5!='CS200'&&(vCS1EventFirst5!='CS220'&&vCS1EventFirst5!='CS140')||vCS1EventFirst5=='CS260'||(
		FND?.CityDetails?.City!=current_Body.Event?.Location?.CityDetails?.City||!FND?.CityDetails?.City)))
		{
			FND_E310_03= 'XXXX'
		}


		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '    '
			}
			'B4' {
				if( vCS1EventCodeConversion){
					'E157_03' vCS1EventCodeConversion
				}

				'E373_04' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)

				'E161_05' util.convertDateTime(current_Body.Event?.EventDT?.LocDT, xmlDateTimeFormat, HHmm)
				
				if(['CS040','CS060','CS070'].contains(vCS1Event)){
					'E159_06' POL_E310_03
				}else if(['CS130','CS120','CS160'].contains(vCS1Event)){
					'E159_06' POD_E310_03
				}else{
					'E159_06' default_E159_06
				}
				
				if(current_Body.Container?.ContainerNumber){
					'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4)
				}

				if(current_Body.Container?.ContainerNumber){
					'E207_08' util.substring(current_Body.Container?.ContainerNumber,5,current_Body.Container?.ContainerNumber.length()) //only give 5
				}

				if (vCS1Event== 'CS010') {
					'E578_09' 'E'
				}else if (vCS1Event == 'CS180') {
					'E578_09' 'L'
				}else if (vCS1Event == 'CS210' && !current_Body.Container?.ContainerStatus) {
					'E578_09' 'E'
				}else if (vCS1Event != 'CS210' && !current_Body.Container?.ContainerStatus) {
					'E578_09' 'L'
				}else if(current_Body.Container?.ContainerStatus=='Laden'){
					'E578_09' 'L'
				}else if(current_Body.Container?.ContainerStatus=='Empty'){
					'E578_09' 'E'
				}

				if(Int_Cde){
					'E24_10' vCS2EventCodeConversion
				}

				if(current_Body.Event?.Location?.LocationName){
					if(util.substring(current_Body.Event?.Location?.LocationName, 1, 30).trim()=='Ho Chi Minh (Cat Lai)'){
						'E310_11' 'Ho Chi Minh'
					}else{
						'E310_11' util.substring(current_Body.Event?.Location?.LocationName, 1, 30).trim()
					}
				}else if(current_Body.Event?.Location?.CityDetails?.City){
					'E310_11' util.substring(current_Body.Event?.Location?.CityDetails.City, 1, 30).trim()
				}else if(current_Body.Event?.Location?.LocationCode?.UNLocationCode){
					'E310_11'current_Body.Event?.Location?.LocationCode?.UNLocationCode
				}

				if(current_Body.Event?.Location?.LocationName||current_Body.Event?.Location?.CityDetails.City){
					'E309_12' 'CI'
				}else if(current_Body.Event?.Location?.LocationCode?.UNLocationCode){
					'E309_12' 'UN'
				}

				'E761_13'  util.substring(current_Body.Container?.ContainerCheckDigit, 1,2)
			}
			//Loop N9
			//for avoid duplicated N9
			def SNs = []

			def Legal_IntCde=['CGN','CCP','FWD','NPT','SHP']
			int count=1
			current_Body.Party.findAll{StringUtil.isNotEmpty(it?.PartyType)&&StringUtil.isNotEmpty(it?.CarrierCustomerCode)&&Legal_IntCde.contains(it?.PartyType)}.groupBy{it?.CarrierCustomerCode?.trim()}.each{carrierCustomerCode,PartyGroup ->
				if(count<=30){
					count++
					'N9' {
						'E128_01' '4F'
						'E127_02' util.substring(carrierCustomerCode,1,10).trim()
					}
				}
			}
			
			if(count<30){
				//ContainerNumber
				if ((current_Body.Container?.ContainerNumber || current_Body.Container?.ContainerCheckDigit)) {
					String eq = ''

					if(current_Body.Container?.ContainerNumber){
						eq = util.substring(current_Body?.Container?.ContainerNumber,1,10)
						if(current_Body.Container?.ContainerCheckDigit){
							eq = eq + current_Body.Container?.ContainerCheckDigit
						}
					}else{
						eq = current_Body.Container?.ContainerCheckDigit
					}
					'N9' {
						'E128_01' 'EQ'
						'E127_02' util.substring(eq,1,30)
					}
				}
				//BookingNumber
				current_Body.BookingGeneralInfo.findAll{StringUtil.isNotEmpty(it?.CarrierBookingNumber)}.groupBy{it?.CarrierBookingNumber?.trim()}.each { bookingNumber, current_BookingGeneralInfoGroup ->
					'N9' {
						'E128_01' 'BN'
						'E127_02' util.substring(bookingNumber,1,30)
					}
				}
				
				
				//BLNumber
				current_Body?.BLGeneralInfo.findAll{StringUtil.isNotEmpty(it?.BLNumber)}.groupBy{it?.BLNumber?.trim()}.each { BLNumber, current_BLGeneralInfo ->
					'N9' {
						println("here")
						'E128_01' 'BM'
						'E127_02' util.substring(BLNumber,1,30)
					}
				}

				current_Body.Container.Seal.each { current_Seal ->
					if (current_Seal?.SealNumber &&current_Seal?.SealNumber?.trim().length() >= 4 ) {
						SNs.add(current_Seal?.SealNumber?.trim())
						'N9' {
							'E128_01' 'SN'
							'E127_02' current_Seal?.SealNumber.trim()
						}
					}
				}

				//CarrierSCAC
				'N9'{
					'E128_01' 'SCA'
					'E127_02' 'OOLU'
				}

				//FalicityCode
				//select ext_cde from b2b_cde_conversion where tp_id=? and msg_type_id=? and dir_id=? and convert_type_id=? and int_cde=?";
				String Facility_IxtCode=current_Body?.Route?.LastPOD?.Facility?.FacilityCode
				//println(Facility_IxtCode)
				String Facility_ExtCode=util.getConversion('GTNEXUS','CT','O','FacilityCode',Facility_IxtCode,conn)
				if(Facility_IxtCode&&Facility_ExtCode&&firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT){
					'N9'{
						'E128_01' 'TT'
						'E127_02' Facility_ExtCode
					}
				}
			}

			if (shipDir == 'I') {
				//println("In")
				'Q2'{
					if ((vCS1Event=='CS080'||vCS1Event=='CS100')&&Record_VESSEL_VOYAGEREC) {
						//println("1")
						if(Record_VESSEL_VOYAGEREC[0]?.SVVD?.Discharge?.LloydsNumber){
							'E597_01'util.substring(Record_VESSEL_VOYAGEREC[0]?.SVVD?.Discharge?.LloydsNumber, 1, 7)
						}

						if(Record_VESSEL_VOYAGEREC[0]?.SVVD?.Discharge?.RegistrationCountryCode){
							'E26_02' Record_VESSEL_VOYAGEREC[0].SVVD?.Discharge?.RegistrationCountryCode
						}

						if( Record_VESSEL_VOYAGEREC[0]?.SVVD?.Discharge?.Voyage||Record_VESSEL_VOYAGEREC[0]?.SVVD?.Discharge?.Direction){
							'E55_09'  Record_VESSEL_VOYAGEREC[0]?.SVVD?.Discharge?.Voyage+Record_VESSEL_VOYAGEREC[0]?.SVVD?.Discharge?.Direction
						}

						'E897_12' 'L'

						if (Record_VESSEL_VOYAGEREC[0]?.SVVD?.Discharge?.VesselName) {
							'E182_13' util.substring(Record_VESSEL_VOYAGEREC[0].SVVD?.Discharge?.VesselName,1,28).trim()
						}

					}else if((vCS1Event=='CS090'||vCS1Event=='CS110')&&Record_VESSEL_VOYAGEREC1){
						//println("2")
						if(Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Discharge?.LloydsNumber){
							'E597_01'util.substring(Record_VESSEL_VOYAGEREC1[0].SVVD?.Discharge?.LloydsNumber, 1, 7)
						}

						if(Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Discharge?.RegistrationCountryCode){
							'E26_02' Record_VESSEL_VOYAGEREC1[0].SVVD?.Discharge?.RegistrationCountryCode
						}

						if( Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Discharge?.Voyage||Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Discharge?.Direction){
							'E55_09'  Record_VESSEL_VOYAGEREC1[0].SVVD?.Discharge?.Voyage+Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Discharge?.Direction
						}

						'E897_12' 'L'

						if (Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Discharge?.VesselName) {
							'E182_13' util.substring(Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Discharge?.VesselName,1,28).trim()
						}
					}else{
						//println("3")
						if(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber){
							'E597_01' util.substring(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber, 1, 7)
						}

						if(lastOceanLeg?.SVVD.Discharge?.RegistrationCountryCode){
							'E26_02' lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode
						}

						if(lastOceanLeg?.SVVD?.Discharge?.Voyage||lastOceanLeg?.SVVD?.Discharge?.Direction){
							'E55_09' lastOceanLeg?.SVVD?.Discharge?.Voyage+lastOceanLeg?.SVVD?.Discharge?.Direction
						}

						'E897_12' 'L'

						if (lastOceanLeg?.SVVD?.Discharge?.VesselName) {
							'E182_13' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName,1,28).trim()
						}
					}
				}
			} else if (shipDir == 'O') {
				//println("Out")
				'Q2'{
					if ((vCS1Event=='CS080'||vCS1Event=='CS100')&&Record_VESSEL_VOYAGEREC) {
						if(Record_VESSEL_VOYAGEREC[0]?.SVVD?.Loading?.LloydsNumber){
							'E597_01'util.substring(Record_VESSEL_VOYAGEREC[0]?.SVVD?.Loading?.LloydsNumber, 1, 7)
						}

						if(Record_VESSEL_VOYAGEREC[0]?.SVVD?.Loading?.RegistrationCountryCode){
							'E26_02' Record_VESSEL_VOYAGEREC[0]?.SVVD?.Loading?.RegistrationCountryCode
						}

						if(Record_VESSEL_VOYAGEREC[0]?.SVVD?.Loading?.Voyage||Record_VESSEL_VOYAGEREC[0]?.SVVD?.Loading?.Direction){
							'E55_09'  Record_VESSEL_VOYAGEREC[0]?.SVVD?.Loading?.Voyage+Record_VESSEL_VOYAGEREC[0]?.SVVD?.Loading?.Direction
						}

						'E897_12' 'L'

						if (Record_VESSEL_VOYAGEREC[0]?.SVVD?.Loading?.VesselName) {
							'E182_13' util.substring(Record_VESSEL_VOYAGEREC[0]?.SVVD?.Loading?.VesselName,1,28).trim()
						}

					}else if((vCS1Event=='CS090'||vCS1Event=='CS110')&&Record_VESSEL_VOYAGEREC1){
						if(Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Loading?.LloydsNumber){
							'E597_01'util.substring(Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Loading?.LloydsNumber, 1, 7)
						}

						if(Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Loading?.RegistrationCountryCode){
							'E26_02' Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Loading?.RegistrationCountryCode
						}

						if(Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Loading?.Voyage||Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Loading?.Direction){
							'E55_09'  Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Loading?.Voyage+Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Loading?.Direction
						}

						'E897_12' 'L'

						if (Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Loading?.VesselName) {
							'E182_13' util.substring(Record_VESSEL_VOYAGEREC1[0]?.SVVD?.Loading?.VesselName,1,28).trim()
						}
					}else{
						if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber){
							'E597_01' util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 7)
						}

						if(firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode){
							'E26_02' firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode
						}

						if(firstOceanLeg?.SVVD?.Loading?.Voyage||firstOceanLeg?.SVVD?.Loading?.Direction){
							'E55_09' firstOceanLeg?.SVVD?.Loading?.Voyage+firstOceanLeg?.SVVD?.Loading?.Direction
						}

						'E897_12' 'L'

						if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
							'E182_13' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName,1,28).trim()
						}
					}
				}
			}
			//R4

			if(POR){
				'Loop_R4' {
					'R4' {
						'E115_01' 'R'

						if (POR?.CityDetails?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						}else if(POR?.CityDetails?.LocationCode?.SchedKDCode){
							'E309_02' POR?.CityDetails?.LocationCode?.SchedKDType
						}else if(!POR?.CityDetails?.LocationCode?.UNLocationCode&&!POR?.CityDetails?.LocationCode?.SchedKDCode) {
							'E309_02' 'ZZ'
						}

						if (POR.CityDetails?.LocationCode?.UNLocationCode) {
							'E310_03' POR?.CityDetails?.LocationCode?.UNLocationCode
						}else if(POR.CityDetails?.LocationCode?.SchedKDCode) {
							'E310_03' POR?.CityDetails?.LocationCode?.SchedKDCode
						}else if(!POR.CityDetails?.LocationCode?.UNLocationCode&&!POR?.CityDetails?.LocationCode?.SchedKDCode){
							'E310_03' 'XXXX'
						}

						if (POR?.CityDetails?.City) {
							'E114_04' POR?.CityDetails?.City
						}

						if (POR?.CSStandardCity?.CSCountryCode){
							'E26_05' POR?.CSStandardCity?.CSCountryCode
						}

						if (POR?.CSStandardCity?.CSStateCode) {
							'E156_08' POR?.CSStandardCity?.CSStateCode
						}
					}

					LocDT porDTM = null
					def isAct = false
					if (current_Body?.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT) {
						porDTM = current_Body.Route?.CargoReceiptDT?.find{it.attr_Indicator== 'A'}?.LocDT
						isAct = true
					} else{
						if (current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT && current_Body.BookingGeneralInfo[0]?.Haulage?.OutBound == 'C') {
							porDTM = current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT
						}else if (current_Body?.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.OutBound == 'M') {
							porDTM = current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}.LocDT
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
			}
			if(lastPOD&&!(vCS1EventFirst5=='CS180'&&FND_E310_03==POD_E310_03)){
				'Loop_R4' {
					'R4' {
						'E115_01' 'D'

						if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						}else if(!lastPOD?.Port?.LocationCode?.UNLocationCode&&lastPOD?.Port?.LocationCode?.SchedKDCode){
							'E309_02' lastPOD?.Port?.LocationCode?.SchedKDType.trim()
						}else if (!lastPOD?.Port?.LocationCode?.UNLocationCode&&!lastPOD?.Port?.LocationCode?.SchedKDCode&&
						(['CS120','CS130','CS160','CS190','CS080','CS140','CS260','CS220'].contains(vCS1EventFirst5)&&((lastPOD?.Port?.PortName==current_Body.Event?.Location?.LocationName
						&&lastPOD?.Port?.PortName)||(current_Body.Event?.Location?.CityDetails?.City==lastPOD?.Port?.City&&lastPOD?.Port?.City))))
						{
							'E309_02' 'ZZ'
						}else if (!lastPOD?.Port?.LocationCode?.UNLocationCode&&!lastPOD?.Port?.LocationCode?.SchedKDCode&&(!['CS120','CS130','CS160','CS190','CS080'].contains(vCS1EventFirst5)
						&&(!['CS140','CS260','CS220'].contains(vCS1EventFirst5)||((lastPOD?.Port?.PortName!=current_Body.Event?.Location?.LocationName)
						||!lastPOD?.Port?.PortName)&&(current_Body.Event?.Location?.CityDetails?.City!=lastPOD?.Port?.City||!lastPOD?.Port?.City))))
						{
							'E309_02' 'ZZ'
						}


						if(POD_E310_03){
							if(POD_E310_03=='VNCLI'){
								'E310_03'  'VNSGN'
							}else{
								'E310_03'  POD_E310_03
							}
						}


						if (lastPOD?.Port?.PortName) {
							if(lastPOD?.Port?.PortName=='Ho Chi Minh (Cat Lai)'){
								'E114_04'  'Ho Chi Minh'
							}else{
								'E114_04' util.substring(lastPOD?.Port?.PortName,1,24).trim()
							}
						}

						if (lastPOD?.Port?.CSCountryCode){
							'E26_05' lastPOD?.Port?.CSCountryCode
						}

						if (lastPOD?.CSStateCode) {
							'E156_08' lastPOD?.CSStateCode
						}
					}
					LocDT podArrivalDTA =lastOceanLeg?.POD?.ArrivalDT.find{it.attr_Indicator == 'A'}?.LocDT
					LocDT podArrivalDTE =lastOceanLeg?.POD?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT
					if (podArrivalDTA) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(podArrivalDTA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDTA, xmlDateTimeFormat, HHmm)
						}
					} else if (podArrivalDTE) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(podArrivalDTE, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDTE, xmlDateTimeFormat, HHmm)
						}
					}
				}
			}
			if(firstPOL){
				'Loop_R4' {
					'R4' {
						'E115_01' 'L'

						if (firstPOL?.Port?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						}else if (firstPOL?.Port?.LocationCode?.SchedKDCode) {
							'E309_02' firstPOL?.Port?.LocationCode?.SchedKDType
						}else if (!firstPOL?.Port?.LocationCode?.SchedKDCode&&!firstPOL?.Port?.LocationCode?.UNLocationCode&&
						(!['CS030','CS040','CS060','CS070','CS110','CS047'].contains(vCS1EventFirst5))){
							'E309_02' 'ZZ'
						}else if (!firstPOL?.Port?.LocationCode?.SchedKDCode&&!firstPOL?.Port?.LocationCode?.UNLocationCode&&
						(['CS030','CS040','CS060','CS070','CS110','CS047'].contains(vCS1EventFirst5))) {
							'E309_02' 'ZZ'
						}

						if(POL_E310_03){
							'E310_03' POL_E310_03
						}


						if (firstPOL?.Port?.PortName&&firstPOL?.Port?.PortName?.contains("(")){
							'E114_04' util.substring(firstPOL?.Port?.PortName,1,12).trim()
						}else if(firstPOL?.Port?.PortName){
							'E114_04'firstPOL.Port?.PortName
						}

						if(firstPOL?.Port?.CSCountryCode){
							'E26_05' firstPOL.Port.CSCountryCode
						}

						if (firstPOL?.CSStateCode) {
							'E156_08' firstPOL.CSStateCode
						}
					}
					LocDT polDepartureDtA = firstOceanLeg?.POL?.DepartureDT.find{it.attr_Indicator == 'A'}?.LocDT
					LocDT polDepartureDtE = firstOceanLeg?.POL?.DepartureDT.find{it.attr_Indicator == 'E'}?.LocDT
					if (polDepartureDtA) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, HHmm)
						}
					} else if (polDepartureDtE) {
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, HHmm)
						}
					}
				}
			}
			if(FND){
				'Loop_R4' {
					'R4' {
						'E115_01' 'E'

						if (FND?.CityDetails?.LocationCode?.UNLocationCode) {
							'E309_02' 'UN'
						}else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {
							'E309_02' FND?.CityDetails?.LocationCode?.SchedKDType
						}else if(!FND?.CityDetails?.LocationCode?.UNLocationCode&&!FND?.CityDetails?.LocationCode?.SchedKDCode&&
						(['CS200','CS220','CS2140','CS260'].contains(vCS1EventFirst5)&&(FND?.CityDetails?.City==current_Body.Event?.Location?.CityDetails?.City&&FND?.CityDetails?.City)))
						{
							'E309_02' 'ZZ'
						}else if(!FND?.CityDetails?.LocationCode?.UNLocationCode&&!FND?.CityDetails?.LocationCode?.SchedKDCode&&
						(!['CS200','CS220','CS2140','CS260'].contains(vCS1EventFirst5)||(FND?.CityDetails?.City!=current_Body.Event?.Location?.CityDetails?.City||!FND?.CityDetails?.City)))
						{
							'E309_02' 'ZZ'
						}

						if(FND_E310_03){
							if(FND_E310_03=='VNCLI'){
								'E310_03' 'VNSGN'
							}else{
								'E310_03'  FND_E310_03
							}
						}

						if (FND?.CityDetails?.City) {
							'E114_04'FND?.CityDetails?.City
						}else if(lastPOD?.Port?.PortName) {
							'E114_04' lastPOD?.Port?.PortName?.trim()
						}

						if (FND?.CSStandardCity?.CSCountryCode){
							'E26_05' FND?.CSStandardCity?.CSCountryCode
						}
						if (FND?.CSStandardCity?.CSStateCode) {
							'E156_08' FND?.CSStandardCity?.CSStateCode
						}
					}
					LocDT fndDatetime = null
					def fndisAct = false

					if (current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT) {
						fndDatetime = current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT
						fndisAct = true
					} else if (current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'A'}?.LocDT) {
						fndDatetime = current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'A'}?.LocDT
						fndisAct = true
					} else if (current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT) {
						fndDatetime = current_Body?.Route.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT
					} else if (current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator == 'E'}?.LocDT) {
						fndDatetime = current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator== 'E'}?.LocDT
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
			}
			'SE' {
				//SE-01 is auto line counter by BelugaOcean, so here need place a space is ok
				'E96_01' ' '
				'E329_02' '    '
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
		def headerMsgDT = util.convertDateTime(ct.Header?.MsgDT?.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		//long ediIsaCtrlNumber = ctrlNos[0]
		//long ediGroupCtrlNum = ctrlNos[1]
		def txnErrorKeys = []
//		generateEDIHeader(outXml, appSessionId, ct, currentSystemDt)

		//duplication -- CT special logic
		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
			
		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		int Bodyindex=0;
		for(Body b : blDupBodies ){
			if(b?.Event?.CS1Event?.equals('CS200') && !(b?.Route?.FND?.CSStandardCity?.CSCountryCode?.equals('CA') || b?.Route?.FND?.CSStandardCity?.CSCountryCode?.equals('US')))
				blDupBodies.remove(Bodyindex);
				Bodyindex++;
		};
		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

		//start body looping
		ct.Body.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event
			def eventExtCde = util.getConversion('VFCGTN', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

			//prep checking
			List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex, eventCS2Cde, eventExtCde)

			if (errorKeyList.size() == 0) {
				//pass validateBeforeExecution
				//main mapping
				generateBody(current_Body, outXml)
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

		String result = "";
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			//if exists one txn without error, then return result
			result = writer?.toString()
		}

		writer.close();
		csuploadWriter.close()
		bizKeyWriter.close()

		return result;
	}

	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();

		def Location=current_Body.Event?.Location
		def CityDetails=current_Body.Event?.Location?.CityDetails

		String containsCustomerCode='no'
		def Legal_IntCde=['ANP','BPT','CCP','CGN','FWD','NPT','OTH','SHP']
		def CustomerCode=false
		if(current_Body?.Party?.findAll{Legal_IntCde.contains(it?.PartyType) &&StringUtil.isNotEmpty(it?.CarrierCustomerCode)&&containsCustomerCode=='no'&&it?.CarrierCustomerCode!=it?.PartyType}?.size()<0){
			containsCustomerCode='yes'
		}else{
			containsCustomerCode='no'
		}
		//		// error cases
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body?.Event?.EventDT?.LocDT, false, null, errorKeyList)	
		missingEventLocationCode(eventCS2Cde, current_Body?.Event, false, null, errorKeyList)
		missingContainerNumberAndLength(eventExtCde,current_Body?.Container,false, '- Missing Equipment Number', errorKeyList)
		if(!current_Body?.BookingGeneralInfo?.findAll{StringUtil.isNotEmpty(it?.CarrierBookingNumber)}?.size()){
			missingBookingNumber(eventCS2Cde, null, false, null, errorKeyList)
		}
		for(Party p : current_Body?.Party ){
			missingPartyCustomerName(eventCS2Cde, p, false, '- Event not for Cost Plus', errorKeyList)
		}
		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)

		missingCustomerCode(eventCS2Cde, containsCustomerCode, true, null, errorKeyList)
		


		return errorKeyList;
	}
	
	private static final String YES = 'YES'
	private static final String NO = 'NO'
	private static final String C = 'C'
	private static final String ERROR_SUPPORT = 'ES'
	private static final String ERROR_COMPLETE = 'EC'
	
	void missingEventLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Event Event, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(Event.Location?.LocationName)&&StringUtil.isEmpty(Event.Location?.CityDetails?.City)&&StringUtil.isEmpty(Event.Location?.LocationCode?.UNLocationCode))
		{
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing Event Location Code']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingContainerNumberAndLength(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Container container, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (container?.findAll{(StringUtil.isNotEmpty(it?.ContainerNumber)||StringUtil.isNotEmpty(it?.ContainerCheckDigit))&&(it?.ContainerNumber?.length()+it?.ContainerCheckDigit?.length())>10}?.size() == 0) {
				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Container Number']
				errorKeyList.add(errorKey);
		}
	}
	
	void missingBookingNumber(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.BookingGeneralInfo BookingGeneralInfo, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(BookingGeneralInfo?.CarrierBookingNumber))
		{
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing Booking Number']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingPartyCustomerName(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Party Party, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(Party.findAll{(it?.PartyName?.toUpperCase()?.equals("WORLD MARKET") || it?.PartyName?.toUpperCase()?.equals("COST PLUS"))&& StringUtil.isEmpty(it?.PartyLevel)&&eventCS2Cde.equals('CS090')})
		{
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing Party Customer Name']
			errorKeyList.add(errorKey)
		}
	}
	
	void missingCustomerCode(String eventCS2Cde, String  CustomerCode, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(CustomerCode.equals('yes'))
		{
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Customer Code']
			errorKeyList.add(errorKey)
		}
	}

	public boolean pospValidation() {

	}
	private void generateEDIHeader(MarkupBuilder outXml, def appSessionId, ContainerMovement ContainerMovement, def currentSystemDt) {
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_InterchangeMessageID', ContainerMovement.Header.InterchangeMessageID);
		cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_CarrierSCAC', ContainerMovement.Body[0]?.GeneralInformation?.SCAC);

		outXml.'ISA' {
			'I01_01' '00'
			'I02_02' '          '
			'I03_03' '00'
			'I04_04' '          '
			'I05_05' 'ZZ'
			'I06_06' 'CARGOSMART     '
			'I05_07' 'ZZ'
			'I07_08' 'GTNEXUS        '
			'I08_09' currentSystemDt.format("yyMMdd")
			'I09_10' currentSystemDt.format(HHmm)
			'I10_11' 'U'
			'I11_12' '00401'
			'I12_13' '###edi_Isa_Ctrl_Number###'
			'I13_14' '0'
			'I14_15' 'P'
			'I15_16' '>'
		}
		outXml.'GS' {
			'E479_01' 'QO'
			'E142_02' 'CARGOSMART'
			'E124_03' 'GTNEXUS'
			'E373_04' currentSystemDt.format(yyyyMMdd)
			'E337_05' currentSystemDt.format(HHmm)
			'E28_06' '###edi_Group_Ctrl_Number###'
			'E455_07' 'X'
			'E480_08' '004010'
		}

	}

	private void generateEDITail(MarkupBuilder outXml, def outputBodyCount) {
		outXml.'GE' {
			'E97_01' outputBodyCount
			'E28_02' '###edi_Group_Ctrl_Number###'
		}
		outXml.'IEA' {
			'I16_01' '1'
			'I12_02' '###edi_Isa_Ctrl_Number###'
		}
	}

}






