package cs.b2b.mapping.scripts

import groovy.xml.MarkupBuilder
import sun.font.TrueTypeFont

import java.awt.event.ItemEvent
import java.lang.ref.ReferenceQueue.Null;
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.List;
import java.util.Map;

import javax.rmi.CORBA.Util;

import cs.b2b.core.common.util.StringUtil;
import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.mapping.bean.ct.BookingGeneralInfo;
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.ct.FND
import cs.b2b.core.mapping.bean.ct.FirstPOL
import cs.b2b.core.mapping.bean.ct.LastPOD
import cs.b2b.core.mapping.bean.ct.OceanLeg
import cs.b2b.core.mapping.bean.ct.POR
import cs.b2b.core.mapping.bean.ct.Party
import cs.b2b.core.mapping.util.XmlBeanParser

import org.apache.commons.lang3.SerializationUtils

/**
 * @author WYNNELI
 * DSGOODS CT initialize on 20161 
 */
public class CUS_CS2CTXML_315_JJGTN {

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
			pre.setMaxRows(100);
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

		def vCS1EventCodeConversion = util.getConversion('JJGTN', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)


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

		def default_E159_06=null

		if (EventLocationCodeUN){
			default_E159_06 = EventLocationCodeUN
		}else if( EventSchedKDCode){
			default_E159_06= EventSchedKDCode
		}else{
			default_E159_06= '9999'
		}


		def POL_E310_03=null

		if (firstPOL?.Port?.LocationCode?.UNLocationCode)
		{
			POL_E310_03= firstPOL?.Port?.LocationCode?.UNLocationCode
		}else if (firstPOL?.Port?.LocationCode?.SchedKDCode)
		{
			POL_E310_03= firstPOL?.Port?.LocationCode?.SchedKDCode
		}else if (!firstPOL?.Port?.LocationCode?.UNLocationCode&&!firstPOL?.Port?.LocationCode?.SchedKDCode&&
		(['CS030', 'CS040', 'CS060', 'CS070', 'CS110', 'CS047'].contains(vCS1EventFirst5))){
			POL_E310_03= '9999'
		}
		else if (!firstPOL?.Port?.LocationCode?.UNLocationCode&&!firstPOL?.Port?.LocationCode?.SchedKDCode&&
		(!['CS030', 'CS040', 'CS060', 'CS070', 'CS110', 'CS047'].contains(vCS1EventFirst5))){
			POL_E310_03= 'XXXX'
		}


		def POD_E310_03=null


		if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
			POD_E310_03= lastPOD.Port.LocationCode.UNLocationCode
		} else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
			POD_E310_03= lastPOD.Port.LocationCode.SchedKDCode
		}else if(!lastPOD?.Port?.LocationCode?.UNLocationCode&&!lastPOD?.Port?.LocationCode?.SchedKDCode&&((['CS120', 'CS130', 'CS160', 'CS190', 'CS080'].contains(vCS1EventFirst5))||((['CS140', 'CS260', 'CS220'].contains(vCS1EventFirst5))&&((lastPOD?.Port?.PortName==
		current_Body.Event?.Location?.LocationName&&lastPOD?.Port?.PortName)||(current_Body.Event?.Location?.CityDetails?.City==lastPOD?.Port?.City&&lastPOD?.Port?.City)))))
		{
			POD_E310_03= '9999'
		}else if(!lastPOD?.Port?.LocationCode?.UNLocationCode&&!lastPOD?.Port?.LocationCode?.SchedKDCode&&
		((!['CS120', 'CS130', 'CS160', 'CS190', 'CS080'].contains(vCS1EventFirst5))&&((!['CS140', 'CS260', 'CS220'].contains(vCS1EventFirst5))||((lastPOD?.Port?.PortName!=
		current_Body.Event?.Location?.LocationName||!lastPOD?.Port?.PortName)&&(current_Body.Event?.Location?.CityDetails?.City!=lastPOD?.Port?.City||!lastPOD?.Port?.City)))))
		{
			POD_E310_03= 'XXXX'
		}


		def FND_E310_03=null

		if(FND?.CityDetails?.LocationCode?.UNLocationCode) {
			FND_E310_03= FND?.CityDetails?.LocationCode?.UNLocationCode
		} else if (FND?.CityDetails?.LocationCode?.SchedKDCode) {
			FND_E310_03= FND.CityDetails.LocationCode.SchedKDCode
		}else if(!FND?.CityDetails?.LocationCode?.UNLocationCode&&!FND.CityDetails?.LocationCode?.SchedKDCode&&
		(vCS1EventFirst5=='CS200'||(vCS1EventFirst5=='CS220'||vCS1EventFirst5=='CS140')||vCS1EventFirst5=='CS260'&&(FND?.CityDetails?.City==current_Body.Event?.Location?.CityDetails?.City&&FND?.CityDetails?.City)))
		{
			FND_E310_03= '9999'
		}else if(!FND?.CityDetails?.LocationCode?.UNLocationCode&&!FND.CityDetails?.LocationCode?.SchedKDCode&&
		(vCS1EventFirst5!='CS200'&&(vCS1EventFirst5!='CS220'&&vCS1EventFirst5!='CS140')||vCS1EventFirst5=='CS260'||(
		FND?.CityDetails?.City!=current_Body.Event?.Location?.CityDetails?.City||!FND?.CityDetails?.City)))
		{
			FND_E310_03= 'XXXX'
		}

		if(current_Body.Route?.OceanLeg){
			firstOceanLeg = current_Body.Route?.OceanLeg[0]
			lastOceanLeg = current_Body.Route?.OceanLeg[-1]
		}
		outXml.'Loop_ST' {
			'ST' {
				'E143_01' '315'
				'E329_02' '    '
			}
			//B4
			'B4' {
				if( vCS1EventCodeConversion){
					'E157_03' vCS1EventCodeConversion
				}

				'E373_04' util.convertDateTime(current_Body?.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd)

				'E161_05' util.convertDateTime(current_Body?.Event?.EventDT?.LocDT, xmlDateTimeFormat, HHmm)

				if(['CS040', 'CS060', 'CS070'].contains(vCS1Event)){
					'E159_06' POL_E310_03
				}else if(['CS130', 'CS120', 'CS160'].contains(vCS1Event)){
					'E159_06' POD_E310_03
				}else{
					'E159_06' default_E159_06
				}

				if(current_Body.Container?.ContainerNumber){
					'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4)
				}

				if(current_Body.Container?.ContainerNumber){
					'E207_08' util.substring(current_Body.Container?.ContainerNumber, 5, 6)
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

				//				if(current_Body.Container.CarrCntrSizeType){
				//					'E24_10' vCS2EventCodeConversion
				//				}
				if(Int_Cde){
					'E24_10' vCS2EventCodeConversion
				}

				if(current_Body.Event?.Location?.LocationName){
					//					if(util.substring(current_Body.Event.Location.LocationName, 1, 30).trim()=='Ho Chi Minh (Cat Lai)'){
					//						'E310_11' 'Ho Chi Minh'
					//					}else{
					'E310_11' util.substring(current_Body.Event.Location.LocationName, 1, 30).trim()
					//					}
				}else if(current_Body.Event?.Location?.CityDetails?.City){
					'E310_11' util.substring(current_Body.Event.Location.CityDetails.City, 1, 30).trim()
				}else if(current_Body.Event?.Location?.LocationCode?.UNLocationCode){
					'E310_11'current_Body.Event.Location.LocationCode.UNLocationCode
				}

				if(current_Body.Event?.Location?.LocationName||current_Body.Event?.Location?.CityDetails?.City){
					'E309_12' 'CI'
				}else if(current_Body.Event?.Location?.LocationCode?.UNLocationCode){
					'E309_12' 'UN'
				}

				if(current_Body.Container?.ContainerCheckDigit){
					'E761_13'  current_Body.Container?.ContainerCheckDigit
				}
			}



			def SNs = []

			//LegalParty
			def Legal_IntCde=['CGN', 'CCP', 'FWD', 'NPT', 'SHP']
			int count=1
			current_Body.Party.findAll{StringUtil.isNotEmpty(it.PartyType)&&StringUtil.isNotEmpty(it.CarrierCustomerCode)&&Legal_IntCde.contains(it.PartyType)}.groupBy{it.CarrierCustomerCode?.trim()}.each{carrierCustomerCode,PartyGroup ->
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
						eq = util.substring(current_Body.Container.ContainerNumber,1,10)
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
				current_Body.BookingGeneralInfo.findAll{StringUtil.isNotEmpty(it.CarrierBookingNumber)}.groupBy{it.CarrierBookingNumber?.trim()}.each { bookingNumber, current_BookingGeneralInfoGroup ->
					'N9' {
						'E128_01' 'BN'
						'E127_02' util.substring(bookingNumber,1,30)
					}
				}

				//BLNumber
				current_Body.BLGeneralInfo.findAll{StringUtil.isNotEmpty(it.BLNumber)}.groupBy{it.BLNumber.trim()}.each { blNumber, current_BookingGeneralInfoGroup ->
					'N9' {
						'E128_01' 'BM'
						'E127_02' util.substring(blNumber,1,30)
					}
				}


				//ExtReference
				//				current_Body.ExternalReference.findAll{StringUtil.isNotEmpty(it.ReferenceNumber)&&it.CSReferenceType=='SR'}.groupBy{it.ReferenceNumber.trim()}.each{ referenceNumber,referenceNumberGroup->
				current_Body.ExternalReference.findAll{StringUtil.isNotEmpty(it.ReferenceNumber)&&it.CSReferenceType=='SR'}.groupBy{it.ReferenceNumber.trim()}.each{ referenceNumber,referenceNumberGroup->
					'N9' {
						'E128_01' 'SI'
						'E127_02' util.substring(referenceNumber,1,30).trim()
					}
				}
				//SealNumber
				String flag=false
				//			def Invalid_char1=["#","\$","%" ,"&"," ' ","(",")","*",",","-",".","/","<",">","@","\\","^","`","~"]

				String Invalid_char="# \$ % & ' ( ) * , - . / < > @ \\ ^ ` ~"
				current_Body.Container.Seal.each { current_Seal ->
					if (current_Seal?.SealNumber &&current_Seal?.SealNumber?.trim()?.length() >= 4) {
						SNs.add(current_Seal.SealNumber?.trim())
						if(current_Seal?.SealNumber?.contains(Invalid_char)){
							flag=true
						}
						if(flag.equals("false")){
							'N9' {
								'E128_01' 'SN'
								'E127_02' util.substring(current_Seal?.SealNumber,1,12).trim()
							}
						}
					}
				}


				//				current_Body.Container.Seal.each { current_Seal ->
				//					if (current_Seal?.SealNumber &&current_Seal?.SealNumber?.trim().length() >= 4 ) {
				//						SNs.add(current_Seal?.SealNumber?.trim())
				//						'N9' {
				//							'E128_01' 'SN'
				//							'E127_02' current_Seal?.SealNumber.trim()
				//						}
				//					}
				//				}

				//CarrierSCAC
				'N9'{
					'E128_01' 'SCA'
					'E127_02' current_Body?.GeneralInformation?.SCAC
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
			String s=null;
			if (shipDir == 'I') {
				'Q2'{
					if(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber){
						'E597_01' util.substring(lastOceanLeg.SVVD.Discharge.LloydsNumber, 1, 7)
					}

					if(lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode){
						'E26_02'lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode
					}

					if(lastOceanLeg?.SVVD?.Discharge?.Voyage&&lastOceanLeg?.SVVD?.Discharge?.Direction){
						s=lastOceanLeg?.SVVD?.Discharge?.Voyage+lastOceanLeg?.SVVD?.Discharge?.Direction
					}else if(lastOceanLeg?.SVVD?.Discharge?.Voyage){
						s=lastOceanLeg.SVVD.Discharge.Voyage
					}else if(lastOceanLeg?.SVVD?.Discharge?.Direction){
						s=lastOceanLeg?.SVVD?.Discharge?.Direction
					}
					if(s){
						'E55_09' s
					}

					'E897_12' 'L'

					if (lastOceanLeg?.SVVD?.Discharge?.VesselName) {
						'E182_13' util.substring(lastOceanLeg.SVVD?.Discharge?.VesselName,1,28).trim()
					}
				}
			} else if (shipDir == 'O') {
				'Q2'{
					if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber){
						'E597_01' util.substring(firstOceanLeg?.SVVD?.Loading?.LloydsNumber, 1, 7)
					}

					if(firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode){
						'E26_02' firstOceanLeg.SVVD.Loading.RegistrationCountryCode
					}

					if(firstOceanLeg?.SVVD?.Loading?.Voyage&&firstOceanLeg?.SVVD?.Loading?.Direction){
						s=firstOceanLeg?.SVVD?.Loading?.Voyage+firstOceanLeg?.SVVD?.Loading?.Direction
					}else if(firstOceanLeg?.SVVD?.Loading?.Direction){
						s=firstOceanLeg.SVVD?.Loading?.Direction
					}else if(firstOceanLeg?.SVVD?.Loading?.Voyage){
						s=firstOceanLeg.SVVD?.Loading?.Voyage
					}
					if(s){
						'E55_09' s
					}
					//					if(firstOceanLeg?.SVVD?.Loading?.Voyage||firstOceanLeg?.SVVD?.Loading?.Direction){
					//						'E55_09' firstOceanLeg.SVVD.Loading.Voyage+firstOceanLeg.SVVD.Loading?.Direction
					//					}

					'E897_12' 'L'

					if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
						'E182_13' util.substring(firstOceanLeg?.SVVD?.Loading?.VesselName,1,28).trim()
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
							if (!POR?.CityDetails?.LocationCode?.SchedKDType) {
								
							} else {
								'E309_02' POR.CityDetails.LocationCode.SchedKDType
							}
						}else if(!POR?.CityDetails?.LocationCode?.UNLocationCode&&!POR?.CityDetails?.LocationCode?.SchedKDCode) {
							'E309_02' 'ZZ'
						}

						if (POR.CityDetails?.LocationCode?.UNLocationCode) {
							'E310_03'  POR.CityDetails.LocationCode.UNLocationCode
						}else if(POR.CityDetails?.LocationCode?.SchedKDCode) {
							'E310_03'  POR.CityDetails.LocationCode.SchedKDCode
						}else if(!POR.CityDetails?.LocationCode?.UNLocationCode&&!POR?.CityDetails?.LocationCode?.SchedKDCode){
							'E310_03'  'XXXX'
						}


						if (POR?.CityDetails?.City) {
							'E114_04' util.substring(POR.CityDetails.City,1,24).trim()
						}

						if (POR?.CSStandardCity?.CSCountryCode){
							'E26_05' POR.CSStandardCity.CSCountryCode
						}

						if (POR?.CSStandardCity?.CSStateCode) {
							'E156_08' POR.CSStandardCity.CSStateCode
						}
					}
					LocDT porDTM = null
					def isAct = false
					if (current_Body.Route.CargoReceiptDT.find{it.attr_Indicator == 'A'}?.LocDT) {
						porDTM = current_Body?.Route?.CargoReceiptDT?.find{it.attr_Indicator== 'A'}?.LocDT
						isAct = true
					} else{
						if (current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.OutBound == 'C') {
							porDTM = current_Body.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT
						}else if (current_Body?.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.OutBound == 'M') {
							porDTM = current_Body.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT
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
						} else if(lastPOD?.Port?.LocationCode?.SchedKDCode){
							if (!lastPOD?.Port?.LocationCode?.SchedKDType) {
								
							} else {
								'E309_02' lastPOD?.Port?.LocationCode?.SchedKDType?.trim()
							}							
						} else {
							'E309_02' 'ZZ'
						}

						if(lastPOD?.Port?.LocationCode?.UNLocationCode)
						{
							'E310_03' lastPOD?.Port?.LocationCode?.UNLocationCode
						}else if(lastPOD?.Port?.LocationCode?.SchedKDCode)
						{
							'E310_03' lastPOD?.Port?.LocationCode?.SchedKDCode
						}else if( (['CS120', 'CS130', 'CS160', 'CS190', 'CS080'].contains(vCS1Event))
						|| ( ['CS140', 'CS220', 'CS260'].contains(vCS1Event)
						&&  (	 lastPOD?.Port?.PortName?.equals(current_Body?.Event?.Location?.LocationName) && StringUtil.isNotEmpty(lastPOD?.Port?.PortName)
						|| lastPOD?.Port?.City?.equals(current_Body?.Event?.Location?.CityDetails?.City) && StringUtil.isNotEmpty(lastPOD?.Port?.City) )
						)  )
						{
							'E310_03' '9999'
						}else
						{
							'E310_03' 'XXXX'
						}

						if (lastPOD?.Port?.PortName) {
							//							if(lastPOD?.Port?.PortName =='Ho Chi Minh (Cat Lai)'){
							//								'E114_04'  'Ho Chi Minh'
							//							}else{
							'E114_04' util.substring(lastPOD.Port.PortName,1,24).trim()
							//							}
						}

						if (lastPOD?.Port?.CSCountryCode){
							'E26_05' lastPOD.Port.CSCountryCode
						}

						if (lastPOD?.CSStateCode) {
							'E156_08' lastPOD.CSStateCode
						}
					}
					LocDT podArrivalDTA =lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
					LocDT podArrivalDTE =lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT
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
							if (! firstPOL?.Port?.LocationCode?.SchedKDType) {
								
							} else {
								'E309_02' firstPOL.Port.LocationCode.SchedKDType
							}							
						}else {
							'E309_02' 'ZZ'
						}

						if(POL_E310_03){
							'E310_03' POL_E310_03
						}

						if(firstPOL?.Port?.PortName){
							'E114_04' firstPOL.Port?.PortName
						}

						if(firstPOL?.Port?.CSCountryCode){
							'E26_05' firstPOL.Port.CSCountryCode
						}

						if (firstPOL?.CSStateCode) {
							'E156_08' firstPOL.CSStateCode
						}
					}
					LocDT polDepartureDtA = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT
					LocDT polDepartureDtE = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT
					if(['CS010', 'CS020', 'CS040', 'CS060'].contains(vCS1Event)&&polDepartureDtE){
						'DTM' {
							'E374_01' '139'
							'E373_02' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, HHmm)
						}
					}else{
						if(polDepartureDtA){
							'DTM' {
								'E374_01' '140'
								'E373_02' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, HHmm)
							}
						}else if(polDepartureDtE){
							'DTM' {
								'E374_01' '139'
								'E373_02' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, yyyyMMdd)
								'E337_03' util.convertDateTime(polDepartureDtE, xmlDateTimeFormat, HHmm)
							}
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
						}else if (FND.CityDetails?.LocationCode?.SchedKDCode) {
							'E309_02' FND.CityDetails.LocationCode.SchedKDType
						}else if(!FND?.CityDetails?.LocationCode?.UNLocationCode&&!FND?.CityDetails?.LocationCode?.SchedKDCode&&
						(['CS200', 'CS220', 'CS2140', 'CS260'].contains(vCS1Event)&&(FND?.CityDetails?.City==current_Body?.Event?.Location?.CityDetails?.City&&FND?.CityDetails?.City)))
						{
							'E309_02' 'ZZ'
						}else if(!FND?.CityDetails?.LocationCode?.UNLocationCode&&!FND?.CityDetails?.LocationCode?.SchedKDCode&&
						(!['CS200', 'CS220', 'CS2140', 'CS260'].contains(vCS1Event)||(FND?.CityDetails?.City!=current_Body?.Event?.Location?.CityDetails?.City||!FND?.CityDetails?.City)))
						{
							'E309_02' 'ZZ'
						}

						if(FND?.CityDetails?.LocationCode?.UNLocationCode)
						{
							'E310_03' FND?.CityDetails?.LocationCode?.UNLocationCode
						}else if(FND.CityDetails?.LocationCode?.SchedKDCode)
						{
							'E310_03' FND.CityDetails?.LocationCode?.SchedKDCode
						}else if(['CS200'].contains(vCS1Event)||(['CS140'].contains(vCS1Event)||['CS220'].contains(vCS1Event))||['CS260'].contains(vCS1Event)&&
						(FND.CityDetails?.City==current_Body.Event?.Location?.CityDetails?.City&&FND.CityDetails?.City))
						{
							'E310_03' '9999'
						}else
						{
							'E310_03' 'XXXX'
						}

						//						if(FND_E310_03){
						//							if(FND_E310_03=='VNCLI'){
						//								'E310_03' 'VNSGN'
						//							}else{
						//								'E310_03'  FND_E310_03
						//							}
						//						}


						if (FND?.CityDetails?.City) {
							'E114_04' util.substring(FND?.CityDetails?.City,1,24).trim()
						}else if(lastPOD?.Port?.PortName) {
							'E114_04' util.substring(lastPOD.Port.PortName,1,24).trim()
						}

						if (FND?.CSStandardCity?.CSCountryCode){
							'E26_05' FND.CSStandardCity.CSCountryCode
						}

						if (FND?.CSStandardCity?.CSStateCode) {
							'E156_08' FND?.CSStandardCity?.CSStateCode
						}
					}

					LocDT fndDatetime = null
					def fndisAct = false

					if (current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT&&vCS1Event=='CS195') {
						fndDatetime = current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT
						fndisAct = true
					}else if(current_Body.Route?.CargoDeliveryDT?.find{it.attr_Indicator=='A'}?.LocDT) {
						fndDatetime = current_Body.Route.CargoDeliveryDT.find{it.attr_Indicator == 'A'}.LocDT
						fndisAct = true
					}else if (current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT&&current_Body.BookingGeneralInfo[0]?.Haulage?.InBound=='M'&&current_Body.Route?.ArrivalAtFinalHub.find{it.attr_Indicator == 'E'}?.LocDT!=0) {
						fndDatetime = current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT
					}else if(current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator=='E'}?.LocDT&&current_Body.BookingGeneralInfo[0]?.Haulage?.InBound=='C'&&current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator=='E'}?.LocDT!=0){
						fndDatetime = current_Body.Route.CargoDeliveryDT.find{it.attr_Indicator=='E'}.LocDT
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
		def headerMsgDT = util.convertDateTime(ct.Header.MsgDT.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
		//long ediIsaCtrlNumber = ctrlNos[0]
		//long ediGroupCtrlNum = ctrlNos[1]
		def txnErrorKeys = []
//		generateEDIHeader(outXml, appSessionId, ct, currentSystemDt)

		//duplication -- CT special logic

		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs('JJGTN', conn)
		//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
		//		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

		//		 Duplicate	O	315	Transaction	7
		//		List<Body> tmp = []
		//		ct?.Body?.each{
		//			tmp.add(it)
		//			if (! (it?.Event?.CS1Event.equals('CS190') && it?.BookingGeneralInfo[0]?.Haulage?.InBound.equals('M')) ) {
		//				duplicatedPairs.remove('CS190')
		//				cs.b2b.core.mapping.bean.ct.Body duplBody = (cs.b2b.core.mapping.bean.ct.Body)SerializationUtils.clone(it)
		//				duplBody.Event.CS1Event = 'CS190D'
		//				tmp.add(duplBody)
		//			}
		//		}
		//		ct?.Body?.eachWithIndex{b, i ->
		//			if (b?.Event?.CS1Event?.equals('CS190') && !(b?.BookingGeneralInfo[0]?.Haulage?.InBound.equals('M')) ) {
		//				ct.Body.remove(i)
		//				i++
		//			}
		//		}


		//DuplicateCS200	O	315	Transaction	1
		//		ct?.Body?.each{
		//			if ( (it?.Event?.CS1Event.equals('CS200')) ){
		//				duplicatedPairs.remove('CS200')
		//			}
		//		}
		
		//Reduce DB Queries
		if (!cusType)
			cusType = getCutomerCodeMap(conn);

		List<Body> bodies =	ctUtil.CTEventDuplication(ct.Body, duplicatedPairs)

		//		 Duplicate	O	315	Transaction	7
		for(int i = 0;i < bodies.size(); ) {
			if (bodies[i]?.Event?.CS1Event?.equals('CS190D') && !(bodies[i]?.BookingGeneralInfo[0]?.Haulage?.InBound?.toUpperCase()?.equals('M')) )
				bodies.remove(i)
			else
				i++
		}
 
		
		//start body looping
		bodies.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body?.Event?.CS1Event
			def eventExtCde = util.getConversion('JJGTN', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

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


		//promote csupload and bizkey to session
		ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
		ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
		ctUtil.promoteAssoInterchangeMessageIDToSession(appSessionId,ct.Header.InterchangeMessageID);
		if(ct.Body[0]?.GeneralInformation?.SCAC)ctUtil.promoteAssoCarrierSCACToSession(appSessionId,ct.Body[0]?.GeneralInformation?.SCAC);

		String result = "";
		if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
			//if exists one txn without error, then return result
			result = writer?.toString()
		}

		writer.close();
		csuploadWriter.close()
		bizKeyWriter.close()

		//		println result;
		return result;
	}

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
	
	// reduce DB queries
	static Map<String, String> cusType = null;

	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();

		def Location=current_Body.Event.Location
		def CityDetails=current_Body.Event.Location.CityDetails

		def containsCustomerCode='no'
		def Legal_IntCde=['CGN', 'CCP', 'FWD', 'NPT', 'SHP', 'ANP', 'BPT', 'OTH']

		for (Party party : current_Body?.Party) {
			def customerType = cusType.get(party.PartyType)
			if (Legal_IntCde.contains(party.PartyType) && !(['BL', 'BK'].contains(party.PartyLevel)) && (['SH', 'FW', 'CN'].contains(customerType))) {
				if( party.CarrierCustomerCode) 
					containsCustomerCode='yes'				
			}
		}


		//		def tempList = current_Body?.Party?.findAll{
		//			Legal_IntCde.contains(it.PartyType)&&it.PartyLevel!='BL'&&it.PartyLevel!='BK'&&
		//					(getCutomercode(it.PartyType,conn)=='SH'||getCutomercode(it.PartyType,conn)=='FW'||getCutomercode(it.PartyType,conn)=='CN') }
		//		if (tempList.size() > 0  ) {
		//			containsCustomerCode = 'yes'
		//		}

		// CSUpload	O	B4	03-157	1	MapCSUploadData
		if (!eventExtCde || eventExtCde.equals('XX')) {
			ctUtil.EventIsNotSubscribedByParnter(eventCS2Cde, eventExtCde, false, null, errorKeyList)
			ctUtil.EventCodeCSInternalValueIsEmpty(eventCS2Cde, eventExtCde, true, null, errorKeyList)
			ctUtil.missingEventLocationCode(eventCS2Cde, current_Body?.Event, true, null, errorKeyList)
		}

		// CSUpload	O	B4	04-373	2
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body?.Event?.EventDT?.LocDT, true, null, errorKeyList)

		// CSUpload	O	B4	10-24	2
		def Int_Cde=current_Body.Container?.CarrCntrSizeType
		def vCS2EventCodeConversion=getConversionWithScacAndMsg_Fmt_Id('GTNEXUS','X.12','O','ContainerType',Int_Cde, current_Body?.GeneralInformation?.SCAC, conn)
		if (StringUtil.isEmpty(vCS2EventCodeConversion)) {
			ctUtil.FailToConvertContainerSizeType(eventCS2Cde, current_Body?.Container, true, null, errorKeyList)
		}

		//		CSUpload	O	CheckInput	Exception	24
		//CheckCSUpload  Type:24  missStatusCode
		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body?.Event?.EventDT?.LocDT, true, null, errorKeyList)
		ctUtil.missingEventLocationCode(eventCS2Cde, current_Body?.Event, true, null, errorKeyList)
		ctUtil.missingBookingNumber(eventCS2Cde, current_Body?.BookingGeneralInfo, true, null, errorKeyList)
		ctUtil.missingEquipmentNumber(eventCS2Cde, current_Body?.Container, true, null, errorKeyList)
		ctUtil.missingCustomerCode(eventCS2Cde, containsCustomerCode, true, null, errorKeyList)


		//B4-03-157  missEventLocationCode
		//		ctUtil.missEventLocationCode(eventCS2Cde,Location,CityDetails,true, null, errorKeyList)
		//		missingEventLocationCode(eventCS2Cde,Location,CityDetails,true, null, errorKeyList)
		//		missingEventLocationCode(eventCS2Cde,  current_Body?.Event, true, null, errorKeyList)
		//B4-04-373
		//		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body?.Event?.EventDT?.LocDT, true, null, errorKeyList)

		//missContainerNumber
		//		ctUtil.missEquipmentNumber2(eventCS2Cde, current_Body.Container, true, null, errorKeyList)
		//		missingEquipmentNumber(eventCS2Cde, current_Body?.Container, true, null, errorKeyList)


		//missBookingNumber
		//		ctUtil.missBookingNumber(eventCS2Cde, current_Body.BookingGeneralInfo, true, null, errorKeyList)
		//		missingBookingNumber(eventCS2Cde, current_Body.BookingGeneralInfo, true, null, errorKeyList)
		//		if(!current_Body?.BookingGeneralInfo?.findAll{StringUtil.isNotEmpty(it?.CarrierBookingNumber)}?.size()){
		//			missingBookingNumber(eventCS2Cde, null, false, null, errorKeyList)
		//		}


		//Missing Status Event Date
		//		ctUtil.missingEventStatusDate1(eventCS2Cde, current_Body.Event.EventDT.LocDT, true, null, errorKeyList)
		//		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body?.Event?.EventDT?.LocDT, true, null, errorKeyList)


		//missCustomerCode
		//ctUtil.missingCustomerCode(eventCS2Cde,containsCustomerCode,true, null, errorKeyList)

		// error cases
		//		ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body?.Route?.Inbound_intermodal_indicator, false, null, errorKeyList)

		return errorKeyList;
	}

	//	Fail to convert container size type
	void FailToConvertContainerSizeType(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Container container, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? errorMsg + container?.CarrCntrSizeType : 'Fail to convert container size type=' + container?.CarrCntrSizeType]
		errorKeyList.add(errorKey)
	}

	void missingEquipmentNumber(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.Container container, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(util.isEmpty(container?.ContainerNumber) || container?.ContainerNumber?.size() < 10){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Equipment Number']
			errorKeyList.add(errorKey)
		}
	}

	void missingBookingNumber(String eventCS2Cde, List<cs.b2b.core.mapping.bean.ct.BookingGeneralInfo> BookingGeneralInfo, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(BookingGeneralInfo[0]?.CarrierBookingNumber))
		{
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing Booking Number']
			errorKeyList.add(errorKey)
		}
	}


	void missingCustomerCode(String eventCS2Cde, String  CustomerCode, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(CustomerCode.equals('no'))
		{
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Customer Code']
			errorKeyList.add(errorKey)
		}
	}

	void missingEventLocationCode(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Event Event, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isEmpty(Event.Location?.LocationName)&&StringUtil.isEmpty(Event.Location?.CityDetails?.City)&&StringUtil.isEmpty(Event.Location?.LocationCode?.UNLocationCode))
		{
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing Event Location Code']
			errorKeyList.add(errorKey)
		}
	}

	void EventIsNotSubscribedByParnter(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (eventExtCde.equals('XX')) {
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Event is not subscribed by Parnter ! ']
			errorKeyList.add(errorKey)
		}
	}

	void EventCodeCSInternalValueIsEmpty(String eventCS2Cde, String eventExtCde, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList) {
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(eventCS2Cde)) {
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventExtCde + errorMsg : eventCS2Cde + '  - Missing Status Code']
			errorKeyList.add(errorKey)
		}
	}


	public Map<String, String> getCutomerCodeMap(Connection conn) throws Exception {
		Map<String, String> ret = new HashMap()
		if (conn == null)
			return ret;

		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "SELECT int_cde, ext_cde FROM B2B_CDE_CONVERSION WHERE CONVERT_TYPE_ID = 'IxPartyType' AND MSG_TYPE_ID = 'CT' AND DIR_ID = 'O'"
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(util.getDBRowLimit());
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());

			result = pre.executeQuery();

			while (result.next()) {
				ret.put(result.getString(1), result.getString(2));
			}
		} finally {
			if (result != null)
				result.close();
			if (pre != null)
				pre.close();
		}
		return ret;
	}

	//	List<Body> duplicateTransaction7(List<Body> bodies) {
	//		List<Body> res = []
	//		for(Body b : bodies ){
	//			res.add(b)
	//			if (b?.Event?.CS1Event == 'CS190' && b?.BookingGeneralInfo[0]?.Haulage?.InBound == 'M') {
	//				cs.b2b.core.mapping.bean.ct.Body duplBody = (cs.b2b.core.mapping.bean.ct.Body)SerializationUtils.clone(b)
	//				duplBody.Event.CS1Event = 'CS190D'
	//				res.add(duplBody)
	//			}
	//		}
	//		return res
	//	}

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
			'I07_08' 'APLUNET        '
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
			'E124_03' 'APLUNET'
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






