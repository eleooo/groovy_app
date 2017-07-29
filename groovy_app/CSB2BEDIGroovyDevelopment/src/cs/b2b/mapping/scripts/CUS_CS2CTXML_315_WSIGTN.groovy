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
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.ct.FND
import cs.b2b.core.mapping.bean.ct.FirstPOL
import cs.b2b.core.mapping.bean.ct.LastPOD
import cs.b2b.core.mapping.bean.ct.OceanLeg
import cs.b2b.core.mapping.bean.ct.POR
import cs.b2b.core.mapping.bean.ct.Party
import cs.b2b.core.mapping.util.XmlBeanParser

/**
 * @author WYNNELI
 * DSGOODS CT initialize on 20161 
 */
public class CUS_CS2CTXML_315_WSIGTN {

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
	public String getCutomercode(String partytype, Connection conn) throws Exception {
		if (conn == null)
			return "";

		String ret = "";
		PreparedStatement pre = null;
		ResultSet result = null;
		String sql = "SELECT ext_cde FROM B2B_CDE_CONVERSION WHERE CONVERT_TYPE_ID = 'IxPartyType' AND MSG_TYPE_ID = 'CT' AND DIR_ID = 'O' AND INT_CDE=?"
		try {
			pre = conn.prepareStatement(sql);
			pre.setMaxRows(100);
			pre.setQueryTimeout(util.getDBTimeOutInSeconds());
			pre.setString(1, partytype);		
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

	
	public String getvConversionwithSCA(String TP_ID,String convertTypeid,String Intcde,String fmtid,String scacc,Connection conn) throws Exception{
		if(conn==null)
		{return ""}
		String ret=""
		PreparedStatement pre=null
		ResultSet result=null
		String sql="SELECT ext_cde FROM B2B_CDE_CONVERSION  WHERE tp_id = ? AND convert_type_id = ? AND dir_id = 'O' AND Int_Cde = ? AND msg_fmt_id = ? AND scac_cde = ?  "
		try{
			pre=conn.prepareStatement(sql)
			pre.setMaxRows(100)
			pre.setQueryTimeout(util.getDBTimeOutInSeconds())
			pre.setString(1,TP_ID)
			pre.setString(2,convertTypeid)
			pre.setString(3,Intcde)
			pre.setString(4,fmtid)
			pre.setString(5,scacc)
			result=pre.executeQuery()
			if(result.next()){
				ret=result.getString(1)
			}			
		}finally{
			if(result!=null)
			{result.close()}
			if(pre!=null)
			{pre.close()}
		}
		return ret
	}

	public void generateBody(Body current_Body, MarkupBuilder outXml) {

		//CT special fields
		def vCS1Event = current_Body?.Event?.CS1Event

		def vCS1EventFirst5 = util.substring(vCS1Event,1,5)

		def vCS1EventCodeConversion = util.getConversion('WSIGTN', 'CT', 'O', 'EventStatusCode', vCS1Event, conn)

		def shipDir
		if(vCS1EventFirst5=='CS310'||vCS1EventFirst5=='CS090'){
			shipDir='I'
		}else{

			shipDir = util.getConversionWithoutTP('CT', 'O', 'EventDirection', vCS1EventFirst5, conn)	//get the first 5 char, in case duplicate event missing direction.
		}

		def Int_Cde

		def vCS2EventCodeConversion

		if(current_Body?.Container?.CarrCntrSizeType){
			Int_Cde=current_Body.Container?.CarrCntrSizeType
		}

		vCS2EventCodeConversion=getConversionWithScacAndMsg_Fmt_Id('GTNEXUS','X.12','O','ContainerType',Int_Cde,current_Body.GeneralInformation?.SCAC,conn)

		if(current_Body?.Container?.CSContainerSizeType&&vCS2EventCodeConversion.equals("")){
			Int_Cde=current_Body.Container?.CSContainerSizeType
		}

		vCS2EventCodeConversion=getConversionWithScacAndMsg_Fmt_Id('GTNEXUS','X.12','O','ContainerType',Int_Cde,current_Body.GeneralInformation?.SCAC,conn)

		POR POR =  current_Body?.Route?.POR

		FirstPOL firstPOL =  current_Body?.Route?.FirstPOL

		LastPOD lastPOD =  current_Body?.Route?.LastPOD

		FND FND =  current_Body?.Route?.FND

		OceanLeg firstOceanLeg = null

		OceanLeg lastOceanLeg = null


		def EventLocationCodeUN= util.substring(current_Body?.Event?.Location?.LocationCode?.UNLocationCode,1,5).trim()

		def EventSchedKDCode=util.substring(current_Body?.Event?.Location?.LocationCode?.SchedKDCode,1,5).trim()

		def default_E159_06=null

		if (EventLocationCodeUN){
			default_E159_06 = EventLocationCodeUN
		}else if( EventSchedKDCode){
			default_E159_06= EventSchedKDCode
		}else{
			default_E159_06= '9999'
		}

		if(current_Body?.Route?.OceanLeg){
			firstOceanLeg = current_Body.Route.OceanLeg[0]
			lastOceanLeg = current_Body.Route.OceanLeg[-1]
		}

		def FND_E310_03=null

		def POL_E310_03=null

		def POD_E310_03=null

		if(LastPOD){
			if (lastPOD?.Port?.LocationCode?.UNLocationCode) {
				POD_E310_03= util.substring(lastPOD.Port.LocationCode.UNLocationCode,1,5).trim()
			} else if (lastPOD?.Port?.LocationCode?.SchedKDCode) {
				POD_E310_03= util.substring(lastPOD.Port.LocationCode.SchedKDCode,1,5).trim()
			}else if(!lastPOD?.Port?.LocationCode?.UNLocationCode&&!lastPOD?.Port?.LocationCode?.SchedKDCode&&
			((['CS120','CS130','CS160','CS190','CS080'].contains(vCS1EventFirst5))||((['CS140','CS260','CS220'].contains(vCS1EventFirst5))&&((lastPOD?.Port?.PortName==
			current_Body?.Event?.Location?.LocationName&&lastPOD?.Port?.PortName)||(current_Body?.Event?.Location?.CityDetails?.City==lastPOD?.Port?.City&&lastPOD?.Port?.City)))))
			{
				POD_E310_03= '9999'
			}else if(!lastPOD?.Port?.LocationCode?.UNLocationCode&&!lastPOD?.Port?.LocationCode?.SchedKDCode&&
			((!['CS120','CS130','CS160','CS190','CS080'].contains(vCS1EventFirst5))&&((!['CS140','CS260','CS220'].contains(vCS1EventFirst5))||((lastPOD?.Port?.PortName!=
			current_Body?.Event?.Location?.LocationName||!lastPOD?.Port?.PortName)&&(current_Body?.Event?.Location?.CityDetails?.City!=lastPOD?.Port?.City||!lastPOD?.Port?.City)))))
			{
				POD_E310_03= 'XXXX'
			}
		}

		if(FND?.CityDetails?.LocationCode?.UNLocationCode) {
			FND_E310_03 =util.substring(FND?.CityDetails?.LocationCode?.UNLocationCode,1,5).trim()
		} else if (FND.CityDetails?.LocationCode?.SchedKDCode) {
			FND_E310_03= util.substring(FND.CityDetails.LocationCode.SchedKDCode,1,5).trim()
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

		if (firstPOL?.Port?.LocationCode?.UNLocationCode)
		{
			POL_E310_03= util.substring(firstPOL?.Port?.LocationCode?.UNLocationCode,1,5).trim()
		}else if (firstPOL?.Port?.LocationCode?.SchedKDCode)
		{
			POL_E310_03= util.substring(firstPOL?.Port?.LocationCode?.SchedKDCode,1,5).trim()
		}else if (!firstPOL?.Port?.LocationCode?.UNLocationCode&&!firstPOL?.Port?.LocationCode?.SchedKDCode&&
		(['CS030','CS040','CS060','CS070','CS110','CS047'].contains(vCS1EventFirst5))){
			POL_E310_03= '9999'
		}
		else if (!firstPOL?.Port?.LocationCode?.UNLocationCode&&!firstPOL?.Port?.LocationCode?.SchedKDCode&&
		(!['CS030','CS040','CS060','CS070','CS110','CS047'].contains(vCS1EventFirst5))){
			POL_E310_03= 'XXXX'
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
				if(util.convertDateTime(current_Body?.Event?.EventDT?.LocDT, xmlDateTimeFormat, yyyyMMdd))
				{'E373_04' util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, yyyyMMdd)}
				if(util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, HHmm))
				{'E161_05' util.convertDateTime(current_Body.Event.EventDT.LocDT, xmlDateTimeFormat, HHmm)}

				if(['CS040','CS060','CS070'].contains(vCS1Event)){
					'E159_06' POL_E310_03
				}else if(['CS130','CS120','CS160'].contains(vCS1Event)){
					'E159_06' POD_E310_03
				}else{
					'E159_06' default_E159_06
				}

				if(current_Body?.Container?.ContainerNumber){
					'E206_07' util.substring(current_Body.Container?.ContainerNumber, 1, 4).trim()
				}

				if(current_Body?.Container?.ContainerNumber){
					if(current_Body.Container?.ContainerNumber?.length()>11){
						'E207_08' util.substring(current_Body.Container?.ContainerNumber,5,6).trim()
					}else{
						'E207_08' util.substring(current_Body.Container?.ContainerNumber,5,current_Body.Container?.ContainerNumber?.length()).trim()
					}
				}

				if (vCS1Event== 'CS010') {
					'E578_09' 'E'
				}else if (vCS1Event == 'CS180') {
					'E578_09' 'L'
				}else if (vCS1Event == 'CS210' && !current_Body?.Container?.ContainerStatus) {
					'E578_09' 'E'
				}else if (vCS1Event != 'CS210' && !current_Body?.Container?.ContainerStatus) {
					'E578_09' 'L'
				}else if(current_Body.Container?.ContainerStatus=='Laden'){
					'E578_09' 'L'
				}else if(current_Body.Container?.ContainerStatus=='Empty'){
					'E578_09' 'E'
				}

				if(vCS2EventCodeConversion){
					'E24_10' vCS2EventCodeConversion
				}

				if(current_Body?.Event?.Location?.LocationName){
						'E310_11' util.substring(current_Body.Event.Location.LocationName, 1, 30).trim()
					}
				else if(current_Body?.Event?.Location?.CityDetails?.City){
					'E310_11' util.substring(current_Body.Event.Location.CityDetails.City, 1, 30).trim()
				}else if(current_Body.Event?.Location?.LocationCode?.UNLocationCode){
					'E310_11' util.substring(current_Body.Event.Location.LocationCode.UNLocationCode,1,5).trim()
				}

				if(current_Body?.Event?.Location?.LocationName||current_Body?.Event?.Location?.CityDetails?.City){
					'E309_12' 'CI'
				}else if(current_Body?.Event?.Location?.LocationCode?.UNLocationCode){
					'E309_12' 'UN'
				}

				if(current_Body?.Container?.ContainerCheckDigit){
					'E761_13'  current_Body.Container?.ContainerCheckDigit
				}
			}

			def SNs = []

			//LegalParty
			def Legal_IntCde=['CGN','CCP','FWD','NPT','SHP']
			def count=1
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
				if ((current_Body?.Container?.ContainerNumber || current_Body?.Container?.ContainerCheckDigit)) {
					String eq = ''

					if(current_Body?.Container?.ContainerNumber){
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
				current_Body.BLGeneralInfo.findAll{StringUtil.isNotEmpty(it.BLNumber)}.groupBy{it.BLNumber.trim()}.each { blNumber, current_BLGeneralInfoGroup ->
					'N9' {
						'E128_01' 'BM'
						'E127_02' current_Body.GeneralInformation.SCAC+util.substring(blNumber,1,26)
					}
				}



				//SealNumber
				String flag=false

				String Invalid_char="# \$ % & ' ( ) * , - . / < > @ \\ ^ ` ~"
				current_Body?.Container?.Seal?.each { current_Seal ->
					if (current_Seal?.SealNumber &&current_Seal?.SealNumber?.trim()?.length() >= 4) {
						SNs.add(current_Seal.SealNumber?.trim())
						if(current_Seal?.SealNumber?.contains(Invalid_char)){
							flag=true
						}
						if(flag.equals("false")){
							'N9' {
								'E128_01' 'SN'
								'E127_02' util.substring(current_Seal.SealNumber,1,12).trim()
							}
						}
					}
				}

				//CarrierSCAC
				'N9'{
					'E128_01' 'SCA'
					'E127_02' current_Body.GeneralInformation.SCAC
				}

				//FalicityCode
				//select ext_cde from b2b_cde_conversion where tp_id=? and msg_type_id=? and dir_id=? and convert_type_id=? and int_cde=?";
				String Facility_IxtCode=current_Body?.Route?.LastPOD?.Facility?.FacilityCode
				String Facility_ExtCode=util.getConversion('GTNEXUS','CT','O','FacilityCode',Facility_IxtCode,conn)
				if(Facility_IxtCode&&Facility_ExtCode&&firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT){
					'N9'{
						'E128_01' 'TT'
						'E127_02' Facility_ExtCode
					}
				}

			}
			String S=null;

			if (shipDir == 'I') {
				'Q2'{
					if(lastOceanLeg?.SVVD?.Discharge?.LloydsNumber){
						'E597_01' util.substring(lastOceanLeg.SVVD.Discharge.LloydsNumber, 1, 7)
					}

					if(lastOceanLeg?.SVVD?.Discharge?.RegistrationCountryCode){
						'E26_02'lastOceanLeg.SVVD.Discharge.RegistrationCountryCode
					}

					if(lastOceanLeg?.SVVD?.Discharge?.Voyage&&lastOceanLeg?.SVVD?.Discharge?.Direction){
						S=util.substring(lastOceanLeg.SVVD.Discharge.Voyage+lastOceanLeg?.SVVD?.Discharge?.Direction,1,17).trim()
					}else if(lastOceanLeg?.SVVD?.Discharge?.Voyage){
						S=lastOceanLeg.SVVD.Discharge.Voyage
					}else if(lastOceanLeg?.SVVD?.Discharge?.Direction){
						S=lastOceanLeg?.SVVD?.Discharge?.Direction
					}
					if(S){
						'E55_09' S
					}

					'E897_12' 'L'

					if (lastOceanLeg?.SVVD?.Discharge?.VesselName) {
						'E182_13' util.substring(lastOceanLeg?.SVVD?.Discharge?.VesselName,1,28).trim()
					}
				}
			} else if (shipDir == 'O') {
				'Q2'{
					if(firstOceanLeg?.SVVD?.Loading?.LloydsNumber){
						'E597_01' util.substring(firstOceanLeg.SVVD.Loading.LloydsNumber, 1, 7).trim()
					}

					if(firstOceanLeg?.SVVD?.Loading?.RegistrationCountryCode){
						'E26_02' firstOceanLeg.SVVD.Loading.RegistrationCountryCode
					}

					if(firstOceanLeg?.SVVD?.Loading?.Voyage&&firstOceanLeg?.SVVD?.Loading?.Direction){
						S=util.substring(firstOceanLeg.SVVD.Loading.Voyage+firstOceanLeg.SVVD.Loading.Direction,1,17).trim()
					}else if(firstOceanLeg?.SVVD?.Loading?.Direction){
						S=firstOceanLeg.SVVD.Loading.Direction
					}else if(firstOceanLeg?.SVVD?.Loading?.Voyage){
						S=firstOceanLeg.SVVD.Loading.Voyage
					}
					if(S){
						'E55_09' S
					}

					'E897_12' 'L'

					if (firstOceanLeg?.SVVD?.Loading?.VesselName) {
						'E182_13' util.substring(firstOceanLeg.SVVD.Loading.VesselName,1,28).trim()
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
						    if(!POR.CityDetails.LocationCode.SchedKDType){
							}else
							{'E309_02' util.substring(POR.CityDetails.LocationCode.SchedKDType,1,2).trim()}
						}else if(!POR?.CityDetails?.LocationCode?.UNLocationCode&&!POR?.CityDetails?.LocationCode?.SchedKDCode) {
							'E309_02' 'ZZ'
						}
						if (POR.CityDetails?.LocationCode?.UNLocationCode) {
							'E310_03' util.substring(POR.CityDetails.LocationCode.UNLocationCode,1,5).trim()
						}else if(POR.CityDetails?.LocationCode?.SchedKDCode) {
							'E310_03' util.substring(POR.CityDetails.LocationCode.SchedKDCode,1,5).trim()
						}else if(!POR.CityDetails?.LocationCode?.UNLocationCode&&!POR?.CityDetails?.LocationCode?.SchedKDCode){
							'E310_03' 'XXXX'
						}

						if (POR?.CityDetails?.City) {
							'E114_04' util.substring(POR.CityDetails.City,1,24).trim()
						}

						if (POR?.CSStandardCity?.CSCountryCode){
							'E26_05' util.substring(POR.CSStandardCity.CSCountryCode,1,2).trim()
						}

						if (POR?.CSStandardCity?.CSStateCode) {
							'E156_08' util.substring(POR.CSStandardCity.CSStateCode,1,2).trim()
						}
					}
					LocDT porDTM = null
					def isAct = false
					if (current_Body?.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT&&current_Body?.Route?.CargoReceiptDT?.find{it.attr_Indicator == 'A'}?.LocDT!=0) {
						porDTM = current_Body?.Route?.CargoReceiptDT?.find{it.attr_Indicator== 'A'}?.LocDT
						isAct = true
					} else{
						if (current_Body?.Route?.FullPickupDT?.find{it.attr_Indicator == 'E'}?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.OutBound == 'C'&&current_Body.Route.FullPickupDT.find{it.attr_Indicator == 'E'}?.LocDT!=0) {
							porDTM = current_Body.Route.FullPickupDT.find{it.attr_Indicator == 'E'}.LocDT
						}else if (current_Body?.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT && current_Body?.BookingGeneralInfo[0]?.Haulage?.OutBound == 'M'&&current_Body?.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT!=0) {
							porDTM = current_Body?.Route?.FullReturnCutoffDT?.find{it.attr_Indicator == 'A'}?.LocDT
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
						}else if(lastPOD?.Port?.LocationCode?.SchedKDCode){
						if(!lastPOD.Port.LocationCode.SchedKDType){				
						}else
							{'E309_02' util.substring(lastPOD?.Port?.LocationCode?.SchedKDType,1,2).trim()}
						}else if (!lastPOD?.Port?.LocationCode?.UNLocationCode&&!lastPOD?.Port?.LocationCode?.SchedKDCode&&
						(['CS120','CS130','CS160','CS190','CS080'].contains(vCS1EventFirst5)||(['CS140','CS260','CS220'].contains(vCS1EventFirst5)&&((lastPOD?.Port?.PortName==current_Body.Event?.Location?.LocationName
						&&lastPOD?.Port?.PortName)||(current_Body.Event?.Location?.CityDetails?.City==lastPOD?.Port?.City&&lastPOD?.Port?.City)))))
						{
							'E309_02' 'ZZ'
						}else
						{
							'E309_02' 'ZZ'
						}
						if(lastPOD?.Port?.LocationCode?.UNLocationCode)
						{
							'E310_03' util.substring(lastPOD?.Port?.LocationCode?.UNLocationCode,1,5).trim()
						}else if(lastPOD?.Port?.LocationCode?.SchedKDCode)
						{
							'E310_03' util.substring(lastPOD?.Port?.LocationCode?.SchedKDCode?.trim(),1,5)
						}else if((['CS120'].contains(vCS1EventFirst5)||['CS130'].contains(vCS1EventFirst5)||['CS160'].contains(vCS1EventFirst5)||['CS190'].contains(vCS1EventFirst5)||['CS080'].contains(vCS1EventFirst5))||
						((['CS140'].contains(vCS1EventFirst5)||['CS220'].contains(vCS1EventFirst5)||['CS260'].contains(vCS1EventFirst5)&&
							((lastPOD?.Port?.City==current_Body.Event?.Location?.CityDetails?.City&&lastPOD?.Port?.City)||lastPOD?.Port?.PortName==current_Body.Event?.Location?.LocationName&&lastPOD?.Port?.PortName))))
						{
							'E310_03' '9999'
						}else
						{
							'E310_03' 'XXXX'
						}					

						if (lastPOD?.Port?.PortName) {
								'E114_04' util.substring(lastPOD.Port.PortName,1,24).trim()
							
						}

						if (lastPOD?.Port?.CSCountryCode){
							'E26_05' util.substring(lastPOD.Port.CSCountryCode,1,2).trim()
						}

						if (lastPOD?.CSStateCode) {
							'E156_08' util.substring(lastPOD.CSStateCode,1,2).trim()
						}
					}
					LocDT podArrivalDTA =lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT
					LocDT podArrivalDTE =lastOceanLeg?.POD?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT

					if (podArrivalDTA&&podArrivalDTA!=0) {
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(podArrivalDTA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(podArrivalDTA, xmlDateTimeFormat, HHmm)
						}
					} else if (podArrivalDTE&&podArrivalDTE!=0) {
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
						if(!firstPOL.Port.LocationCode.SchedKDType){
							}else
							{'E309_02' util.substring(firstPOL.Port.LocationCode.SchedKDType,1,2).trim()}
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

						if(firstPOL?.Port?.PortName){
							'E114_04' util.substring(firstPOL.Port?.PortName,1,24).trim()
						}

						if(firstPOL?.Port?.CSCountryCode){
							'E26_05' util.substring(firstPOL.Port.CSCountryCode,1,2).trim()
						}

						if (firstPOL?.CSStateCode) {
							'E156_08' util.substring(firstPOL.CSStateCode,1,2).trim()
						}
					}
					LocDT polDepartureDtA = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'A'}?.LocDT
					LocDT polDepartureDtE = firstOceanLeg?.POL?.DepartureDT?.find{it.attr_Indicator == 'E'}?.LocDT
					if(polDepartureDtA&&polDepartureDtA!=0){
						'DTM' {
							'E374_01' '140'
							'E373_02' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, yyyyMMdd)
							'E337_03' util.convertDateTime(polDepartureDtA, xmlDateTimeFormat, HHmm)
						}
					}else if(polDepartureDtE&&polDepartureDtE!=0){
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
						if(!FND.CityDetails.LocationCode.SchedKDType){}else
							{'E309_02' util.substring(FND.CityDetails.LocationCode.SchedKDType,1,2).trim()}
						}else if(!FND?.CityDetails?.LocationCode?.UNLocationCode&&!FND?.CityDetails?.LocationCode?.SchedKDCode&&
						(['CS200','CS220','CS2140','CS260'].contains(vCS1EventFirst5)&&(FND.CityDetails?.City==current_Body.Event?.Location?.CityDetails?.City&&FND.CityDetails.City)))
						{
							'E309_02' 'ZZ'
						}else if(!FND?.CityDetails?.LocationCode?.UNLocationCode&&!FND?.CityDetails?.LocationCode?.SchedKDCode&&
						(!['CS200','CS220','CS2140','CS260'].contains(vCS1EventFirst5)||(FND.CityDetails?.City!=current_Body.Event?.Location?.CityDetails?.City||!FND.CityDetails.City)))
						{
							'E309_02' 'ZZ'
						}

						if(FND?.CityDetails?.LocationCode?.UNLocationCode)
						{
							'E310_03' util.substring(FND?.CityDetails?.LocationCode?.UNLocationCode,1,5).trim()
						}else if(FND?.CityDetails?.LocationCode?.SchedKDCode)
						{
							'E310_03' util.substring(FND.CityDetails?.LocationCode?.SchedKDCode,1,5).trim()
						}else if(['CS200'].contains(vCS1EventFirst5)||(['CS140'].contains(vCS1EventFirst5)||['CS220'].contains(vCS1EventFirst5))||['CS260'].contains(vCS1EventFirst5)&&
						(FND?.CityDetails?.City==current_Body?.Event?.Location?.CityDetails?.City&&FND.CityDetails?.City))
						{
							'E310_03' '9999'
						}else
						{
							'E310_03' 'XXXX'
						}
						if (FND?.CityDetails?.City) {
							'E114_04' util.substring(FND?.CityDetails?.City,1,24).trim()
						}else if(lastPOD?.Port?.PortName) {
							'E114_04' util.substring(lastPOD.Port.PortName,1,24).trim()
						}

						if (FND?.CSStandardCity?.CSCountryCode){
							'E26_05' util.substring(FND.CSStandardCity.CSCountryCode,1,2).trim()
						}

						if (FND?.CSStandardCity?.CSStateCode) {
							'E156_08' util.substring(FND?.CSStandardCity?.CSStateCode,1,2).trim()
							
						}
					}

					LocDT fndDatetime = null
					def fndisAct = false


					if (current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT&&vCS1EventFirst5=='CS195') {
						fndDatetime = current_Body.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'A'}?.LocDT
						fndisAct = true
					} else if (current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'A'}?.LocDT&&vCS1EventFirst5=='CS195'&&current_Body.GeneralInformation?.SCAC!='OOLU') {
						fndDatetime = current_Body.Route.FND.ArrivalDT.find{it.attr_Indicator == 'A'}.LocDT
						fndisAct = true
					}else if(current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator=='A'}?.LocDT) {
						fndDatetime = current_Body.Route.CargoDeliveryDT.find{it.attr_Indicator == 'A'}.LocDT
						fndisAct = true
					}else if (current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT&&current_Body?.BookingGeneralInfo[0]?.Haulage?.InBound=='M'&&current_Body.Route?.ArrivalAtFinalHub.find{it.attr_Indicator == 'E'}?.LocDT!=0) {
						fndDatetime = current_Body?.Route?.ArrivalAtFinalHub?.find{it.attr_Indicator == 'E'}?.LocDT
					} else if (current_Body?.Route?.FND?.ArrivalDT?.find{it.attr_Indicator == 'E'}?.LocDT&&current_Body?.GeneralInformation?.SCAC!='OOLU'&&current_Body.BookingGeneralInfo[0]?.Haulage?.InBound=='M'&&current_Body.Route?.FND?.ArrivalDT.find{it.attr_Indicator == 'E'}?.LocDT!=0) {
						fndDatetime = current_Body?.Route?.FND?.ArrivalDT?.find{it?.attr_Indicator=='E'}?.LocDT
					}else if(current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator=='E'}?.LocDT&&current_Body?.BookingGeneralInfo[0]?.Haulage?.InBound=='C'&&current_Body?.Route?.CargoDeliveryDT?.find{it.attr_Indicator=='E'}?.LocDT!=0){
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
		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs('WSIGTN', conn)
		
		List<Body> bodies = ctUtil.CTEventDuplication(ct.Body, duplicatedPairs)

		//start body looping
		
		bodies.eachWithIndex { current_Body, current_BodyIndex ->

			def eventCS2Cde = current_Body.Event.CS1Event
			//println(eventCS2Cde)
			def eventExtCde = util.getConversion('WSIGTN', 'CT', 'O', 'EventStatusCode', eventCS2Cde, conn)

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

		return result;
	}

	public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
		List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();

		// error cases
		def Location=current_Body.Event.Location
		def CityDetails=current_Body.Event.Location.CityDetails
		String containsCustomerCode='no'
		def Legal_IntCde=['CGN','CCP','FWD','NPT','SHP','ANP','BPT','OTH']
			current_Body.Party.findAll{Legal_IntCde.contains(it.PartyType)&&it.PartyLevel!='BL'&&it.PartyLevel!='BK'&&(getCutomercode(it.PartyType,conn)=='SH'||getCutomercode(it.PartyType,conn)=='FW'||
				getCutomercode(it.PartyType,conn)=='CN')}.each{it->if(it.CarrierCustomerCode) {containsCustomerCode='yes'				
			}
		}
//		//CheckCSUpload  Type:24  missStatusCode
		ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, true, null, errorKeyList)
//
//		//B4-03-157  missEventLocationCode
		ctUtil.missingEventLocationCode(eventCS2Cde,current_Body.Event,true, null, errorKeyList)
		//B4-03-157 Type 3 - Event is not subscribed by Parnter 
		ctUtil.eventnotsubcribed(eventCS2Cde,eventExtCde,false,null,errorKeyList)
//		//missContainerNumber
		ctUtil.missingEquipmentNumberWithOutLengthChecking(eventCS2Cde, current_Body.Container, true, null, errorKeyList)
//
//		//missBookingNumber
//		if(!current_Body?.BookingGeneralInfo?.findAll{StringUtil.isNotEmpty(it?.CarrierBookingNumber)}?.size()){
		ctUtil.missingBookingNumber(eventCS2Cde, current_Body?.BookingGeneralInfo, false, null, errorKeyList)
//		}
//
//		//Missing Status Event Date
		ctUtil.missingEventStatusDate(eventCS2Cde, current_Body.Event.EventDT.LocDT, true, null, errorKeyList)
//
//		//missCustomerCode
		ctUtil.missingCustomerCode(eventCS2Cde,containsCustomerCode,true, null, errorKeyList)
//		//missBlNumber
		ctUtil.missingBLNumberWithEventCode(eventCS2Cde,current_Body.BLGeneralInfo[0],false, null, errorKeyList)
//		
		
////		// error cases
		ctUtil.filterIBIntermodal(eventCS2Cde, 'CS260', current_Body.Route.Inbound_intermodal_indicator, false, null, errorKeyList)
//		//failtoconvertcontainersizetype
		if(!getvConversionwithSCA('GTNEXUS','ContainerType',current_Body.Container?.CarrCntrSizeType,'X.12',current_Body.GeneralInformation?.SCAC,conn))
			{ctUtil.failcontainersizetype(current_Body.Container,eventCS2Cde,true,null,errorKeyList)}
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
	
	void eventnotsubcribed(String eventCS2Cde, String ext_cde, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(StringUtil.isNotEmpty(ext_cde)&&ext_cde=='XX')
		{
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Event not subscribed by Partner']
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
	void failcontainersizetype(cs.b2b.core.mapping.bean.ct.Container container,String eventCS2Cde, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' -Fail to convert container size type= '+container?.CarrCntrSizeType]
			errorKeyList.add(errorKey)	
	}
	void missingEquipmentNumberWithOutLengthChecking(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.Container container, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if(util.isEmpty(container.ContainerNumber)){
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Equipment Number']
			errorKeyList.add(errorKey)
		}
	}
	void missingBLNumberWithEventCode(String eventCS2Cde,cs.b2b.core.mapping.bean.ct.BLGeneralInfo blgeneralinfo,boolean isError,String errorMsg,List<Map<String,String>> errorKeyList)
	{
		Map<String,String> errorKey = null
		if(util.isEmpty(blgeneralinfo?.BLNumber)&&(['CS040'].contains(eventCS2Cde)||['CS310'].contains(eventCS2Cde)))
		{
			errorKey = [TYPE: isError? ERROR_SUPPORT:ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + '  - Missing BL Number for'+eventCS2Cde]
			errorKeyList.add(errorKey)
		
		}
		
	}
	void missingEventStatusDate(String eventCS2Cde, cs.b2b.core.mapping.bean.LocDT LocDT, boolean isError, String errorMsg , List<Map<String,String>> errorKeyList){
		Map<String,String> errorKey = null
		if (StringUtil.isEmpty(LocDT?.toString())||LocDT?.toString().trim()=='0') {
			errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Status Event Date']
			errorKeyList.add(errorKey)
		}
	}
//	void missingContainerNumberAndLength(String eventCS2Cde, cs.b2b.core.mapping.bean.ct.Container container, boolean isError, String errorMsg, List<Map<String,String>> errorKeyList){
//		Map<String,String> errorKey = null
//		if (container?.findAll{(StringUtil.isNotEmpty(it?.ContainerNumber)||StringUtil.isNotEmpty(it?.ContainerCheckDigit))&&(it?.ContainerNumber?.length()+it?.ContainerCheckDigit?.length())>10}?.size() == 0) {
//				errorKey = [TYPE: isError? ERROR_SUPPORT : ERROR_COMPLETE, IS_ERROR: isError? YES : NO, VALUE: errorMsg != null? eventCS2Cde + errorMsg : eventCS2Cde + ' - Missing Container Number']
//				errorKeyList.add(errorKey);
//		}
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






