import groovy.util.XmlSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

import java.io.StringWriter
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date
import java.util.TimeZone

String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {
	
	oll.b2b.core.mapping.util.MappingUtil util = new oll.b2b.core.mapping.util.MappingUtil();
	 
	/**
	 * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
	 */
	inputXmlBody = util.removeBOM(inputXmlBody)
	
	/**
	 * Part II: get OLL mapping runtime parameters
	 */
	def ollSessionId = util.getRuntimeParameter("OLLSessionID", runtimeParams);
	def sourceFileName = util.getRuntimeParameter("OLL_OriginalSourceFileName", runtimeParams);
	//	def portId = getRuntimeParameter("OLL_SendPortID", runtimeParams);
	//	def portProperty = getRuntimeParameter("PortProperty", runtimeParams);
	//pmt info
	def TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams);
	def MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
	def DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams);
	def MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams);

	/**
	 * Part III: read xml and prepare output xml
	 */
	//Important: the inputXml is xml root element
	def ZVSHPFL1 = new XmlParser().parseText(inputXmlBody);

	def writer = new StringWriter()
	def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
	outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
	
	
	/**
	 * Part IV: mapping script start from here
	 */

	TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
	def currentSystemDt = new Date()
	
//outXml.inXml.ct0:ContainerMovement.ct0:Body.each { current_ct0:ContainerMovement_ct0:Body ->
//    'ns1:T-315'('xmlns:ct0':'http://www.cargosmart.com/route', 'xmlns:ct1':'http://www.cargosmart.com/common', 'xmlns:eg':'http://example.com/namespace', 'xmlns:fn':'http://www.w3.org/2005/02/xpath-functions', 'xmlns:ns0':'http://www.tibco.com/schemas/transformation-ctxml-315/Schemas/AppProces/Schema.xsd', 'xmlns:ns1':'http://www.tibco.com/ns/Foresight/2010/20', 'xmlns:ns2':'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd', 'xmlns:ns3':'http://www.tibco.com/schemas/MessageConsumer/Shared.Resources/AppErrorReport.xsd', 'xmlns:xs':'http://www.w3.org/2001/XMLSchema', 'xmlns:xsi':'http://www.w3.org/2001/XMLSchema-instance', 'xmlns:xsl':'http://www.w3.org/1999/XSL/Transform')ObjID:'315', ObjType:'t'{
//      'ns1:S-ST_1'ObjID:'ST', ObjOrd:'1', ObjType:'s'{
//        'ns1:E-143_1_01'ObjID:'143', ObjType:'e' current_ct0:ContainerMovement_ct0:Body."315"
//        'ns1:E-329_1_02'ObjID:'329', ObjType:'e' current_ct0:ContainerMovement_ct0:Body."-999"
//      }
//      'ns1:S-B4_2'ObjID:'B4', ObjOrd:'2', ObjType:'s'{
//        'ns1:E-157_2_03'ObjID:'157', ObjType:'e' CdeConversionData.CodeConversion.Record[CONVERT_TYPE_ID=='EventStatusCode' and INT_CDE==event].EXT_CDE
//        'ns1:E-373_2_04'ObjID:'373', ObjType:'e' format-dateTime(currentBody.ct0:Event.ct0:EventDT.ct1:LocDT,'[Y0001][M01][D01]')
//        'ns1:E-161_2_05'ObjID:'161', ObjType:'e' format-dateTime(currentBody.ct0:Event.ct0:EventDT.ct1:LocDT,'[H01][m01]')
//        'ns1:E-159_2_06'ObjID:'159', ObjType:'e' eg:lpad(currentBody.ct0:Event.ct0:Location.ct0:LocationCode.ct1:UNLocationCode," ",5)
//        'ns1:E-206_2_07'ObjID:'206', ObjType:'e' eg:lpad(currentBody.ct0:Container.ct0:ContainerNumber," ",4)
//        'ns1:E-207_2_08'ObjID:'207', ObjType:'e' concat(substring(currentBody.ct0:Container.ct0:ContainerNumber,5),(currentBody.ct0:Container.ct0:ContainerCheckDigit))
//                  if( exists(currentBody.ct0:Container.ct0:ContainerStatus) and eg:trim(currentBody.ct0:Container.ct0:ContainerStatus)=='Empty'){
//            'ns1:E-578_2_09'ObjID:'578', ObjType:'e' 'E'
//          }
//          if( exists(currentBody.ct0:Container.ct0:ContainerStatus) and eg:trim(currentBody.ct0:Container.ct0:ContainerStatus)=='Laden'){
//            'ns1:E-578_2_09'ObjID:'578', ObjType:'e' 'L'
//          }
//          if( event =='CS210'){
//            'ns1:E-578_2_09'ObjID:'578', ObjType:'e' 'E'
//          }
//          else{
//            'ns1:E-578_2_09'ObjID:'578', ObjType:'e' 'L'
//          }
//                'ns1:E-24_2_10'ObjID:'24', ObjType:'e' substring(currentBody.ct0:Container.ct0:CarrCntrSizeType,1,4)
//                  if( exists(currentBody.ct0:Event.ct0:Location.ct0:CityDetails.ct0:City) and eg:trim(currentBody.ct0:Event.ct0:Location.ct0:CityDetails.ct0:City)!==''){
//            'ns1:E-310_2_11'ObjID:'310', ObjType:'e' upper-case(substring(currentBody.ct0:Event.ct0:Location.ct0:CityDetails.ct0:City,1,30))
//          }
//          if( exists(currentBody.ct0:Event.ct0:Location.ct0:LocationName) and eg:trim(currentBody.ct0:Event.ct0:Location.ct0:LocationName)!==''){
//            'ns1:E-310_2_11'ObjID:'310', ObjType:'e' upper-case(substring(currentBody.ct0:Event.ct0:Location.ct0:LocationName,1,30))
//          }
//          if( exists(currentBody.ct0:Event.ct0:Location.ct0:LocationCode.ct1:UNLocationCode) and eg:trim(currentBody.ct0:Event.ct0:Location.ct0:LocationCode.ct1:UNLocationCode)!==''){
//            'ns1:E-310_2_11'ObjID:'310', ObjType:'e' upper-case(currentBody.ct0:Event.ct0:Location.ct0:LocationCode.ct1:UNLocationCode)
//          }
//                'ns1:E-309_2_12'ObjID:'309', ObjType:'e' 'UN'
//      }
//      def blIndex =  current_ct0:ContainerMovement_ct0:Body.position()
//      current_ct0:ContainerMovement_ct0:Body.currentBody.ct0:BLGeneralInfo[blIndex].each { current_currentBody_ct0:BLGeneralInfo ->
//        if( current_currentBody_ct0:BLGeneralInfo.eg:existsAndTrim(current().ct0:BLNumber)!==''){
//          'ns1:S-N9_3'ObjID:'N9', ObjOrd:'3', ObjType:'s'{
//            'ns1:E-128_3_01'ObjID:'128', ObjType:'e' 'BM'
//            'ns1:E-127_3_02'ObjID:'127', ObjType:'e' current_currentBody_ct0:BLGeneralInfo.eg:existsAndTrim(current().ct0:BLNumber)
//          }
//        }
//        if( current_currentBody_ct0:BLGeneralInfo.eg:existsAndTrim(current().ct0:CustomsReferenceNumber)!=='' and eg:existsAndTrim(current().ct0:CustomsReferenceType) == 'IT'){
//          'ns1:S-N9_3'ObjID:'N9', ObjOrd:'3', ObjType:'s'{
//            'ns1:E-128_3_01'ObjID:'128', ObjType:'e' 'IB'
//            'ns1:E-127_3_02'ObjID:'127', ObjType:'e' current_currentBody_ct0:BLGeneralInfo.eg:existsAndTrim(current().ct0:CustomsReferenceNumber)
//          }
//        }
//      }
//      current_ct0:ContainerMovement_ct0:Body.currentBody.ct0:BookingGeneralInfo.each { current_currentBody_ct0:BookingGeneralInfo ->
//        if( current_currentBody_ct0:BookingGeneralInfo.eg:existsAndTrim(current().ct0:CarrierBookingNumber)!==''){
//          'ns1:S-N9_3'ObjID:'N9', ObjOrd:'3', ObjType:'s'{
//            'ns1:E-128_3_01'ObjID:'128', ObjType:'e' 'BN'
//            'ns1:E-127_3_02'ObjID:'127', ObjType:'e' current_currentBody_ct0:BookingGeneralInfo.eg:existsAndTrim(current().ct0:CarrierBookingNumber)
//          }
//        }
//      }
//      if( CdeConversionData.CodeConversion.Record[CONVERT_TYPE_ID=='N9' and INT_CDE=='EQ'].EXT_CDE == 'ContainerNumber'){
//        'ns1:S-N9_3'ObjID:'N9', ObjOrd:'3', ObjType:'s'{
//          'ns1:E-128_3_01'ObjID:'128', ObjType:'e' 'EQ'
//          'ns1:E-127_3_02'ObjID:'127', ObjType:'e' concat(currentBody.ct0:Container.ct0:ContainerNumber,currentBody.ct0:Container.ct0:ContainerCheckDigit)
//        }
//      }
//      if( CdeConversionData.CodeConversion.Record[CONVERT_TYPE_ID=='N9' and INT_CDE=='EQ'].EXT_CDE=='PickupNumber' and currentBody.ct0:Container.ct0:RailPickupNumber !==''){
//        'ns1:S-N9_3'ObjID:'N9', ObjOrd:'3', ObjType:'s'{
//          'ns1:E-128_3_01'ObjID:'128', ObjType:'e' 'P8'
//          'ns1:E-127_3_02'ObjID:'127', ObjType:'e' eg:existsAndTrim(currentBody.ct0:Container.ct0:RailPickupNumber)
//        }
//      }
//      if( exists(currentBody.ct0:Party[ct1:PartyType=='CGN' and eg:trim(ct1:CarrierCustomerCode)!==''])){
//        'ns1:S-N9_3'ObjID:'N9', ObjOrd:'3', ObjType:'s'{
//          'ns1:E-128_3_01'ObjID:'128', ObjType:'e' 'IC'
//          'ns1:E-127_3_02'ObjID:'127', ObjType:'e' eg:trim(currentBody.ct0:Party[ct1:PartyType=='CGN' and eg:trim(ct1:CarrierCustomerCode)!==''][0].ct1:CarrierCustomerCode)
//        }
//      }
//      current_ct0:ContainerMovement_ct0:Body.currentBody.ct0:ExternalReference.each { current_currentBody_ct0:ExternalReference ->
//        if( eg:existsAndTrim(CdeConversionData.CodeConversion.Record[CONVERT_TYPE_ID=='ExternalReference' and INT_CDE==current().ct0:CSReferenceType].EXT_CDE) !==''){
//          'ns1:S-N9_3'ObjID:'N9', ObjOrd:'3', ObjType:'s'{
//            'ns1:E-128_3_01'ObjID:'128', ObjType:'e' CdeConversionData.CodeConversion.Record[CONVERT_TYPE_ID=='ExternalReference' and INT_CDE==current().ct0:CSReferenceType].EXT_CDE
//            'ns1:E-127_3_02'ObjID:'127', ObjType:'e' current_currentBody_ct0:ExternalReference.eg:existsAndTrim(current().ct0:ReferenceNumber)
//          }
//        }
//      }
//      current_ct0:ContainerMovement_ct0:Body.currentBody.ct0:Container.ct0:Seal.each { current_currentBody_ct0:Container_ct0:Seal ->
//        if( current_currentBody_ct0:Container_ct0:Seal.eg:existsAndTrim(current().ct1:SealNumber) !=='' and string_length(current().ct1:SealNumber) >== 4){
//          'ns1:S-N9_3'ObjID:'N9', ObjOrd:'3', ObjType:'s'{
//            'ns1:E-128_3_01'ObjID:'128', ObjType:'e' 'SN'
//            'ns1:E-127_3_02'ObjID:'127', ObjType:'e' current_currentBody_ct0:Container_ct0:Seal.eg:existsAndTrim(current().ct1:SealNumber)
//          }
//        }
//      }
//      def shipDir =  CdeConversionData.CodeConversion.Record[CONVERT_TYPE_ID=='EventDirection' and INT_CDE==event].EXT_CDE
//      def lastOceanLeg =  currentBody.ct0:Route.ct0:OceanLeg[-1]
//      def firstOceanLeg =  currentBody.ct0:Route.ct0:OceanLeg[0]
//              if( shipDir=='I' or (shipDir=='OB' and currentBody.ct0:Route.ct0:FND.ct1:CSStandardCity.ct1:CSContinentCode !=='' and currentBody.ct0:Route.ct0:FND.ct1:CSStandardCity.ct1:CSCountryCode == currentBody.ct0:Event.ct0:Location.ct0:CSStandardCity.ct1:CSCountryCode)){
//          'ns1:S-Q2_4'ObjID:'Q2', ObjOrd:'4', ObjType:'s'{
//            if( eg:existsAndTrim(lastOceanLeg.ct0:SVVD.ct0:Discharge.ct0:LloydsNumber) !==''){
//              'ns1:E-597_4_01'ObjID:'597', ObjType:'e' substring(lastOceanLeg.ct0:SVVD.ct0:Discharge.ct0:LloydsNumber,1,7)
//            }
//            if( eg:existsAndTrim(lastOceanLeg.ct0:SVVD.ct0:Discharge.ct0:RegistrationCountryCode) !==''){
//              'ns1:E-26_4_02'ObjID:'26', ObjType:'e' lastOceanLeg.ct0:SVVD.ct0:Discharge.ct0:RegistrationCountryCode
//            }
//                          if( eg:existsAndTrim(lastOceanLeg.ct0:POL.ct0:DepartureDT[@Indicator=='A'].ct1:LocDT) !==''){
//                'ns1:E-373_4_04'ObjID:'373', ObjType:'e' format-dateTime(lastOceanLeg.ct0:POL.ct0:DepartureDT[@Indicator=='A'].ct1:LocDT,'[Y0001][M01][D01]')
//              }
//              if( eg:existsAndTrim(lastOceanLeg.ct0:POL.ct0:DepartureDT[@Indicator=='E'].ct1:LocDT) !==''){
//                'ns1:E-373_4_04'ObjID:'373', ObjType:'e' format-dateTime(lastOceanLeg.ct0:POL.ct0:DepartureDT[@Indicator=='E'].ct1:LocDT,'[Y0001][M01][D01]')
//              }
//                                      if( eg:existsAndTrim(lastOceanLeg.ct0:POD.ct0:ArrivalDT[@Indicator=='A'].ct1:LocDT) !==''){
//                'ns1:E-373_4_05'ObjID:'373', ObjType:'e' format-dateTime(lastOceanLeg.ct0:POD.ct0:ArrivalDT[@Indicator=='A'].ct1:LocDT ,'[Y0001][M01][D01]')
//              }
//              if( eg:existsAndTrim(lastOceanLeg.ct0:POD.ct0:ArrivalDT[@Indicator=='E'].ct1:LocDT) !==''){
//                'ns1:E-373_4_05'ObjID:'373', ObjType:'e' format-dateTime(lastOceanLeg.ct0:POD.ct0:ArrivalDT[@Indicator=='E'].ct1:LocDT,'[Y0001][M01][D01]')
//              }
//                        if( eg:existsAndTrim(lastOceanLeg.ct0:SVVD.ct0:Discharge.ct0:Voyage) !== '' or  eg:existsAndTrim(lastOceanLeg.ct0:SVVD.ct0:Discharge.ct0:Direction) !==''){
//              'ns1:E-55_4_09'ObjID:'55', ObjType:'e' concat(lastOceanLeg.ct0:SVVD.ct0:Discharge.ct0:Voyage, lastOceanLeg.ct0:SVVD.ct0:Discharge.ct0:Direction)
//            }
//            'ns1:E-127_4_11'ObjID:'127', ObjType:'e' currentBody.ct0:GeneralInformation.ct0:SCAC
//            'ns1:E-897_4_12'ObjID:'897', ObjType:'e' 'L'
//            if( eg:existsAndTrim(lastOceanLeg.ct0:SVVD.ct0:Discharge.ct0:VesselName) !==""){
//              if( lastOceanLeg.ct0:SVVD.ct0:Discharge.ct0:VesselName){
//                'ns1:E-182_4_13'ObjID:'182', ObjType:'e' lastOceanLeg.ct0:SVVD.ct0:Discharge.ct0:VesselName
//              }
//            }
//          }
//        }
//        if( shipDir=='O' or shipDir=='OB'){
//          'ns1:S-Q2_4'ObjID:'Q2', ObjOrd:'4', ObjType:'s'{
//            if( eg:existsAndTrim(firstOceanLeg.ct0:SVVD.ct0:Loading.ct0:LloydsNumber) !==''){
//              'ns1:E-597_4_01'ObjID:'597', ObjType:'e' substring(firstOceanLeg.ct0:SVVD.ct0:Loading.ct0:LloydsNumber,1,7)
//            }
//            if( eg:existsAndTrim(firstOceanLeg.ct0:SVVD.ct0:Loading.ct0:RegistrationCountryCode) !==''){
//              'ns1:E-26_4_02'ObjID:'26', ObjType:'e' firstOceanLeg.ct0:SVVD.ct0:Loading.ct0:RegistrationCountryCode
//            }
//                          if( eg:existsAndTrim(firstOceanLeg.ct0:POL.ct0:DepartureDT[@Indicator=='A'].ct1:LocDT) !==''){
//                'ns1:E-373_4_04'ObjID:'373', ObjType:'e' format-dateTime(firstOceanLeg.ct0:POL.ct0:DepartureDT[@Indicator=='A'].ct1:LocDT,'[Y0001][M01][D01]')
//              }
//              if( eg:existsAndTrim(firstOceanLeg.ct0:POL.ct0:DepartureDT[@Indicator=='E'].ct1:LocDT) !==''){
//                'ns1:E-373_4_04'ObjID:'373', ObjType:'e' format-dateTime(firstOceanLeg.ct0:POL.ct0:DepartureDT[@Indicator=='E'].ct1:LocDT,'[Y0001][M01][D01]')
//              }
//                                      if( eg:existsAndTrim(firstOceanLeg.ct0:POD.ct0:ArrivalDT[@Indicator=='A'].ct1:LocDT) !==''){
//                'ns1:E-373_4_05'ObjID:'373', ObjType:'e' format-dateTime(firstOceanLeg.ct0:POD.ct0:ArrivalDT[@Indicator=='A'].ct1:LocDT,'[Y0001][M01][D01]')
//              }
//              if( eg:existsAndTrim(firstOceanLeg.ct0:POD.ct0:ArrivalDT[@Indicator=='E'].ct1:LocDT) !==''){
//                'ns1:E-373_4_05'ObjID:'373', ObjType:'e' format-dateTime(firstOceanLeg.ct0:POD.ct0:ArrivalDT[@Indicator=='E'].ct1:LocDT,'[Y0001][M01][D01]')
//              }
//                        if( eg:existsAndTrim(firstOceanLeg.ct0:SVVD.ct0:Loading.ct0:Voyage) !== '' or eg:existsAndTrim(firstOceanLeg.ct0:SVVD.ct0:Loading.ct0:Direction) !==''){
//              'ns1:E-55_4_09'ObjID:'55', ObjType:'e' concat(firstOceanLeg.ct0:SVVD.ct0:Loading.ct0:Voyage, firstOceanLeg.ct0:SVVD.ct0:Loading.ct0:Direction)
//            }
//            'ns1:E-127_4_11'ObjID:'127', ObjType:'e' currentBody.ct0:GeneralInformation.ct0:SCAC
//            'ns1:E-897_4_12'ObjID:'897', ObjType:'e' 'L'
//            if( eg:existsAndTrim(firstOceanLeg.ct0:SVVD.ct0:Loading.ct0:VesselName) !==""){
//              if( firstOceanLeg.ct0:SVVD.ct0:Loading.ct0:VesselName){
//                'ns1:E-182_4_13'ObjID:'182', ObjType:'e' firstOceanLeg.ct0:SVVD.ct0:Loading.ct0:VesselName
//              }
//            }
//          }
//        }
//            def POR =  currentBody.ct0:Route.ct0:POR
//      def firstPOL =  currentBody.ct0:Route.ct0:FirstPOL
//      def lastPOD =  currentBody.ct0:Route.ct0:LastPOD
//      def FND =  currentBody.ct0:Route.ct0:FND
//      'ns1:L-R4_6'ObjID:'R4', ObjType:'l'{
//        'ns1:S-R4_6'ObjID:'R4', ObjOrd:'6', ObjType:'s'{
//          'ns1:E-115_6_01'ObjID:'115', ObjType:'e' 'R'
//                      if( eg:existsAndTrim(POR.ct1:CityDetails.ct1:LocationCode.ct1:UNLocationCode) !==''){
//              'ns1:E-309_6_02'ObjID:'309', ObjType:'e' 'UN'
//            }
//            if( eg:existsAndTrim(POR.ct1:CityDetails.ct1:LocationCode.ct1:SchedKDCode) !==''){
//              'ns1:E-309_6_02'ObjID:'309', ObjType:'e' POR.ct1:CityDetails.ct1:LocationCode.ct1:SchedKDType
//            }
//                                if( eg:existsAndTrim(POR.ct1:CityDetails.ct1:LocationCode.ct1:UNLocationCode) !==''){
//              if( POR.ct1:CityDetails.ct1:LocationCode.ct1:UNLocationCode){
//                'ns1:E-310_6_03'ObjID:'310', ObjType:'e' POR.ct1:CityDetails.ct1:LocationCode.ct1:UNLocationCode
//              }
//            }
//            if( eg:existsAndTrim(POR.ct1:CityDetails.ct1:LocationCode.ct1:SchedKDCode) !==''){
//              if( POR.ct1:CityDetails.ct1:LocationCode.ct1:SchedKDCode){
//                'ns1:E-310_6_03'ObjID:'310', ObjType:'e' POR.ct1:CityDetails.ct1:LocationCode.ct1:SchedKDCode
//              }
//            }
//                    if( eg:existsAndTrim(POR.ct1:CityDetails.ct1:City) !==''){
//            'ns1:E-114_6_04'ObjID:'114', ObjType:'e' upper-case(substring(POR.ct1:CityDetails.ct1:City,1,24))
//          }
//          if( eg:existsAndTrim(POR.ct1:CSStandardCity.ct1:CSCountryCode) !=='' and string_length(POR.ct1:CSStandardCity.ct1:CSCountryCode) >== 2){
//            'ns1:E-26_6_05'ObjID:'26', ObjType:'e' POR.ct1:CSStandardCity.ct1:CSCountryCode
//          }
//          if( eg:existsAndTrim(POR.ct1:CSStandardCity.ct1:CSStateCode) !==''){
//            'ns1:E-156_6_08'ObjID:'156', ObjType:'e' POR.ct1:CSStandardCity.ct1:CSStateCode
//          }
//        }
//      }
//      'ns1:L-R4_6'ObjID:'R4', ObjType:'l'{
//        'ns1:S-R4_6'ObjID:'R4', ObjOrd:'6', ObjType:'s'{
//          'ns1:E-115_6_01'ObjID:'115', ObjType:'e' 'L'
//                      if( eg:existsAndTrim(firstPOL.ct1:Port.ct1:LocationCode.ct1:UNLocationCode) !==''){
//              'ns1:E-309_6_02'ObjID:'309', ObjType:'e' 'UN'
//            }
//            if( eg:existsAndTrim(firstPOL.ct1:Port.ct1:LocationCode.ct1:SchedKDCode) !==''){
//              'ns1:E-309_6_02'ObjID:'309', ObjType:'e' firstPOL.ct1:Port.ct1:LocationCode.ct1:SchedKDType
//            }
//                                if( eg:existsAndTrim(firstPOL.ct1:Port.ct1:LocationCode.ct1:UNLocationCode) !==''){
//              'ns1:E-310_6_03'ObjID:'310', ObjType:'e' firstPOL.ct1:Port.ct1:LocationCode.ct1:UNLocationCode
//            }
//            if( eg:existsAndTrim(firstPOL.ct1:Port.ct1:LocationCode.ct1:SchedKDCode) !==''){
//              'ns1:E-310_6_03'ObjID:'310', ObjType:'e' firstPOL.ct1:Port.ct1:LocationCode.ct1:SchedKDCode
//            }
//                    if( eg:existsAndTrim(firstPOL.ct1:Port.ct1:City) !==''){
//            'ns1:E-114_6_04'ObjID:'114', ObjType:'e' upper-case(substring(firstPOL.ct1:Port.ct1:City,1,24))
//          }
//          if( eg:existsAndTrim(firstPOL.ct1:Port.ct1:CSCountryCode) !=='' and string_length(firstPOL.ct1:Port.ct1:CSCountryCode) >== 2){
//            'ns1:E-26_6_05'ObjID:'26', ObjType:'e' firstPOL.ct1:Port.ct1:CSCountryCode
//          }
//          if( eg:existsAndTrim(firstPOL.ct0:CSStateCode) !==''){
//            'ns1:E-156_6_08'ObjID:'156', ObjType:'e' firstPOL.ct0:CSStateCode
//          }
//        }
//                  if( eg:existsAndTrim(firstOceanLeg.ct0:POL.ct0:DepartureDT[0].@Indicator) == 'A' and eg:existsAndTrim(firstOceanLeg.ct0:POL.ct0:DepartureDT[0].ct1:LocDT) !==''){
//            'ns1:S-DTM_7'ObjID:'DTM', ObjOrd:'7', ObjType:'s'{
//              'ns1:E-374_7_01'ObjID:'374', ObjType:'e' '370'
//              'ns1:E-373_7_02'ObjID:'373', ObjType:'e' format-dateTime(firstOceanLeg.ct0:POL.ct0:DepartureDT[0].ct1:LocDT,'[Y0001][M01][D01]')
//              'ns1:E-337_7_03'ObjID:'337', ObjType:'e' format-dateTime(firstOceanLeg.ct0:POL.ct0:DepartureDT[0].ct1:LocDT, '[H01][m01]')
//              'ns1:E-623_7_04'ObjID:'623', ObjType:'e' 'LT'
//            }
//          }
//          if( eg:existsAndTrim(firstOceanLeg.ct0:POL.ct0:DepartureDT[0].@Indicator) == 'E' and eg:existsAndTrim(firstOceanLeg.ct0:POL.ct0:DepartureDT[0].ct1:LocDT) !==''){
//            'ns1:S-DTM_7'ObjID:'DTM', ObjOrd:'7', ObjType:'s'{
//              'ns1:E-374_7_01'ObjID:'374', ObjType:'e' '369'
//              'ns1:E-373_7_02'ObjID:'373', ObjType:'e' format-dateTime(firstOceanLeg.ct0:POL.ct0:DepartureDT[0].ct1:LocDT,'[Y0001][M01][D01]')
//              'ns1:E-337_7_03'ObjID:'337', ObjType:'e' format-dateTime(firstOceanLeg.ct0:POL.ct0:DepartureDT[0].ct1:LocDT, '[H01][m01]')
//              'ns1:E-623_7_04'ObjID:'623', ObjType:'e' 'LT'
//            }
//          }
//              }
//      'ns1:L-R4_6'ObjID:'R4', ObjType:'l'{
//        'ns1:S-R4_6'ObjID:'R4', ObjOrd:'6', ObjType:'s'{
//          'ns1:E-115_6_01'ObjID:'115', ObjType:'e' 'D'
//                      if( eg:existsAndTrim(lastPOD.ct1:Port.ct1:LocationCode.ct1:UNLocationCode) !==''){
//              'ns1:E-309_6_02'ObjID:'309', ObjType:'e' 'UN'
//            }
//            if( eg:existsAndTrim(lastPOD.ct1:Port.ct1:LocationCode.ct1:SchedKDCode) !==''){
//              'ns1:E-309_6_02'ObjID:'309', ObjType:'e' lastPOD.ct1:Port.ct1:LocationCode.ct1:SchedKDType
//            }
//                                if( eg:existsAndTrim(lastPOD.ct1:Port.ct1:LocationCode.ct1:UNLocationCode) !==''){
//              'ns1:E-310_6_03'ObjID:'310', ObjType:'e' lastPOD.ct1:Port.ct1:LocationCode.ct1:UNLocationCode
//            }
//            if( eg:existsAndTrim(lastPOD.ct1:Port.ct1:LocationCode.ct1:SchedKDCode) !==''){
//              'ns1:E-310_6_03'ObjID:'310', ObjType:'e' lastPOD.ct1:Port.ct1:LocationCode.ct1:SchedKDCode
//            }
//                    if( eg:existsAndTrim(lastPOD.ct1:Port.ct1:City) !==''){
//            'ns1:E-114_6_04'ObjID:'114', ObjType:'e' upper-case(substring(lastPOD.ct1:Port.ct1:City,1,24))
//          }
//          if( eg:existsAndTrim(lastPOD.ct1:Port.ct1:CSCountryCode) !=='' and string_length(lastPOD.ct1:Port.ct1:CSCountryCode) >== 2){
//            'ns1:E-26_6_05'ObjID:'26', ObjType:'e' lastPOD.ct1:Port.ct1:CSCountryCode
//          }
//          if( eg:existsAndTrim(lastPOD.ct0:CSStateCode) !==''){
//            'ns1:E-156_6_08'ObjID:'156', ObjType:'e' lastPOD.ct0:CSStateCode
//          }
//        }
//                  if( eg:existsAndTrim(lastOceanLeg.ct0:POD.ct0:ArrivalDT[0].@Indicator) == 'A' and eg:existsAndTrim(lastOceanLeg.ct0:POD.ct0:ArrivalDT[0].ct1:LocDT) !==''){
//            'ns1:S-DTM_7'ObjID:'DTM', ObjOrd:'7', ObjType:'s'{
//              'ns1:E-374_7_01'ObjID:'374', ObjType:'e' '370'
//              'ns1:E-373_7_02'ObjID:'373', ObjType:'e' format-dateTime( lastOceanLeg.ct0:POD.ct0:ArrivalDT[0].ct1:LocDT,'[Y0001][M01][D01]')
//              'ns1:E-337_7_03'ObjID:'337', ObjType:'e' format-dateTime(lastOceanLeg.ct0:POD.ct0:ArrivalDT[0].ct1:LocDT, '[H01][m01]')
//              'ns1:E-623_7_04'ObjID:'623', ObjType:'e' 'LT'
//            }
//          }
//          if( eg:existsAndTrim(lastOceanLeg.ct0:POD.ct0:ArrivalDT[0].@Indicator) == 'E' and eg:existsAndTrim(lastOceanLeg.ct0:POD.ct0:ArrivalDT[0].ct1:LocDT) !==''){
//            'ns1:S-DTM_7'ObjID:'DTM', ObjOrd:'7', ObjType:'s'{
//              'ns1:E-374_7_01'ObjID:'374', ObjType:'e' '369'
//              'ns1:E-373_7_02'ObjID:'373', ObjType:'e' format-dateTime(lastOceanLeg.ct0:POD.ct0:ArrivalDT[0].ct1:LocDT,'[Y0001][M01][D01]')
//              'ns1:E-337_7_03'ObjID:'337', ObjType:'e' format-dateTime(lastOceanLeg.ct0:POD.ct0:ArrivalDT[0].ct1:LocDT, '[H01][m01]')
//              'ns1:E-623_7_04'ObjID:'623', ObjType:'e' 'LT'
//            }
//          }
//                if( currentBody.ct0:BLGeneralInfo.ct0:CustomsReferenceNumber !=='' and currentBody.ct0:BLGeneralInfo.ct0:CustomsReferenceType=='IT'){
//          'ns1:S-DTM_7'ObjID:'DTM', ObjOrd:'7', ObjType:'s'{
//            'ns1:E-374_7_01'ObjID:'374', ObjType:'e' '059'
//            'ns1:E-373_7_02'ObjID:'373', ObjType:'e' format-dateTime(lastOceanLeg.ct0:POD.ct0:ArrivalDT[0].ct1:LocDT,'[Y0001][M01][D01]')
//            'ns1:E-337_7_03'ObjID:'337', ObjType:'e' format-dateTime(lastOceanLeg.ct0:POD.ct0:ArrivalDT[0].ct1:LocDT,'[H01][m01]')
//            'ns1:E-623_7_04'ObjID:'623', ObjType:'e' 'LT'
//          }
//        }
//      }
//      'ns1:L-R4_6'ObjID:'R4', ObjType:'l'{
//        'ns1:S-R4_6'ObjID:'R4', ObjOrd:'6', ObjType:'s'{
//          'ns1:E-115_6_01'ObjID:'115', ObjType:'e' 'E'
//                      if( eg:existsAndTrim(FND.ct1:CityDetails.ct1:LocationCode.ct1:UNLocationCode) !==''){
//              'ns1:E-309_6_02'ObjID:'309', ObjType:'e' 'UN'
//            }
//            if( eg:existsAndTrim(FND.ct1:CityDetails.ct1:LocationCode.ct1:SchedKDCode) !==''){
//              'ns1:E-309_6_02'ObjID:'309', ObjType:'e' FND.ct1:CityDetails.ct1:LocationCode.ct1:SchedKDType
//            }
//                                if( eg:existsAndTrim(FND.ct1:CityDetails.ct1:LocationCode.ct1:UNLocationCode) !==''){
//              'ns1:E-310_6_03'ObjID:'310', ObjType:'e' FND.ct1:CityDetails.ct1:LocationCode.ct1:UNLocationCode
//            }
//            if( eg:existsAndTrim(FND.ct1:CityDetails.ct1:LocationCode.ct1:SchedKDCode) !==''){
//              'ns1:E-310_6_03'ObjID:'310', ObjType:'e' FND.ct1:CityDetails.ct1:LocationCode.ct1:SchedKDCode
//            }
//                    if( eg:existsAndTrim(FND.ct1:CityDetails.ct1:City) !==''){
//            'ns1:E-114_6_04'ObjID:'114', ObjType:'e' upper-case(substring(FND.ct1:CityDetails.ct1:City,1,24))
//          }
//          if( eg:existsAndTrim(FND.ct1:CSStandardCity.ct1:CSCountryCode) !=='' and string_length(FND.ct1:CSStandardCity.ct1:CSCountryCode) >== 2){
//            'ns1:E-26_6_05'ObjID:'26', ObjType:'e' FND.ct1:CSStandardCity.ct1:CSCountryCode
//          }
//          if( eg:existsAndTrim(FND.ct1:CSStandardCity.ct1:CSStateCode) !==''){
//            'ns1:E-156_6_08'ObjID:'156', ObjType:'e' FND.ct1:CSStandardCity.ct1:CSStateCode
//          }
//        }
//      }
//      'ns1:S-SE_9'ObjID:'SE', ObjOrd:'9', ObjType:'s'{
//        'ns1:E-96_9_01'ObjID:'96', ObjType:'e' current_ct0:ContainerMovement_ct0:Body.-999
//        'ns1:E-329_9_02'ObjID:'329', ObjType:'e' current_ct0:ContainerMovement_ct0:Body.-999
//      }
//    }
//  }
	//mapping logic end
		
//	def outxml = XmlUtil.serialize(writer.toString());
//	return outxml;
	
	return writer.toString();
	
}

