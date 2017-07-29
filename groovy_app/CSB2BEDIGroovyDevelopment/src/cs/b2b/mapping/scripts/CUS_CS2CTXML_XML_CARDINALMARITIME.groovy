package cs.b2b.mapping.scripts

import cs.b2b.core.mapping.bean.LocDT
import cs.b2b.core.mapping.bean.ct.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection

/**
 * Created by XIAOTR on 6/30/2017.
 */
class CUS_CS2CTXML_XML_CARDINALMARITIME {


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
    java.util.Date currentDate = new java.util.Date()


    public void generateBody(Body current_Body, Header ctHeader, MarkupBuilder outXml) {


        //CT special fields
        def vCS1Event = current_Body.Event.CS1Event
       // def vCS1EventFirst5 = util.substring(vCS1Event,1,5)
       // String SCAC = current_Body?.GeneralInformation?.SCAC
        def vCS1EventCodeConversion = util.getConversion('CARDINALMARITIME', 'CT', 'O', 'EventCodeType', vCS1Event, conn)
        def vCS1EventDescription = util.getConversion('CARDINALMARITIME', 'CT', 'O', 'EventCodeDescType', vCS1Event, conn)

        OceanLeg firstOceanLeg = null
        OceanLeg lastOcaenLeg = null

        if(current_Body?.Route?.OceanLeg){
            firstOceanLeg = current_Body?.Route?.OceanLeg[0]
            lastOcaenLeg = current_Body?.Route?.OceanLeg[-1]
        }

        outXml.'CargoTracking'{
            // GeneralInfo mapping
            'GeneralInfo'{
                'TransactionInfo'{
                    if(ctHeader.ControlNumber){
                        'BatchNumber' ctHeader.ControlNumber
                    }else{
                        //get control number from db
                        def xmlCtrlNum = util.getXMLControlNumber(TP_ID, 'CT', 'XML', 'CTCS2X', conn)
                        'BatchNumber' xmlCtrlNum
                    }
                    'MessageSender' 'CARGOSMART'
                    'MessageRecipient' 'CARDINAL'
                    'MessageID' 'CTXML'
                    'DateCreated' 'TimeZone':'HKT', currentDate.format("yyyyMMddHHmmss")
                    'Version' '6.4'
                }
                if(["NEW","UPD"].contains(ctHeader.Action)){
                    'Action' 'ADD'
                }
                'SCAC' current_Body?.GeneralInformation?.SCAC

                'Event'{

                    if(vCS1EventCodeConversion){
                        'EventCode'  vCS1EventCodeConversion
                    }

                    if(vCS1EventDescription){
                        'EventDescription' vCS1EventDescription
                    } else if(current_Body?.Event?.EventDescription) {
                        'EventDescription' current_Body?.Event?.EventDescription
                    }
                    def eventDTTimeZone = null
                    if(current_Body?.Event?.EventDT?.LocDT){
                        // map time zone
                        if(current_Body?.Event?.EventDT?.LocDT?.attr_TimeZone){
                            eventDTTimeZone = current_Body?.Event?.EventDT?.LocDT?.attr_TimeZone
                        }else if(current_Body?.Event?.EventDT?.LocDT?.attr_CSTimeZone){
                            eventDTTimeZone = current_Body?.Event?.EventDT?.LocDT?.attr_CSTimeZone
                        }
                        'EventStatusDate' 'TimeZone':eventDTTimeZone, util.convertXmlDateTime(current_Body?.Event?.EventDT?.LocDT,'yyyyMMddHHmmss')
                    }else if(current_Body?.Event?.EventDT?.GMT){

                        'EventStatusDate' 'TimeZone':'GMT',util.convertXmlDateTime(current_Body?.Event?.EventDT?.GMT,'yyyyMMddHHmmss')
                    }

                    if(current_Body?.Event?.Location?.LocationName){
                        'EventLocationName' util.substring(current_Body?.Event?.Location?.LocationName,1,35)
                    }
                    'EventLocationCode' {
                        if(current_Body?.Event?.Location?.LocationCode?.UNLocationCode){
                            'LocationCode' current_Body?.Event?.Location?.LocationCode?.UNLocationCode
                        }
                        'MutuallyDefinedCode'

                        if(current_Body?.Event?.Location?.LocationCode?.UNLocationCode){
                            'UNLocationCode' current_Body?.Event?.Location?.LocationCode?.UNLocationCode
                        }
                        if(current_Body?.Event?.Location?.LocationCode?.SchedKDCode){
//                            def skdType = null
//                            if(current_Body?.Event?.Location?.LocationCode?.SchedKDType){
//                                skdType = current_Body?.Event?.Location?.LocationCode?.SchedKDType
//                            }
                            'SchedKDCode' 'Type': current_Body?.Event?.Location?.LocationCode?.SchedKDType,current_Body?.Event?.Location?.LocationCode?.SchedKDCode
                        }
                    }
                    if(current_Body?.Event?.Location?.CityDetails?.City){
                        'EventCityName'  current_Body?.Event?.Location?.CityDetails?.City
                    }
                    'EventCounty'
                    'EventStateProvince'
                    if(current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode){
                        'EventCountryCode' current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode
                    }

                    if(current_Body?.Event?.Location?.CityDetails?.Country){
                        'EventCountryName' current_Body?.Event?.Location?.CityDetails?.Country
                    }else {
                        'EventCountryName' util.getCS2MasterCity4CountryNameByCountryCode(current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode,conn)
                    }
                    if(current_Body?.GeneralInformation?.TransportMode){
                        'EventTransportMode'  current_Body?.GeneralInformation?.TransportMode
                    }

                    if(current_Body?.Event?.CurrentShipmentLeg && current_Body?.Event?.CurrentShipmentLeg?.toBigDecimal()>0){
                        'CurrentShipmentLegs' current_Body?.Event?.CurrentShipmentLeg
                    }

                    if(current_Body?.GeneralInformation?.TransportCarrier){
                        'EventTransportCarrier' current_Body?.GeneralInformation?.TransportCarrier
                    }
                    if(current_Body?.Container?.ContainerStatus =='Empty'){
                        'ContainerStatus' 'E'
                    }else if(current_Body?.Container?.ContainerStatus =='Laden'){
                        'ContainerStatus' 'F'
                    }

                }


            }
            // ShipmentDetails mapping
            'ShipmentDetails'{
                // Container Info mapping
                'ContainerInfo'{
                    'Container'{
                        if(current_Body?.Container?.ContainerNumber){
//                            def checkDigit = null
//                            if(current_Body?.Container?.ContainerCheckDigit){
//                                checkDigit = current_Body?.Container?.ContainerCheckDigit
//                            }
                            'ContainerNumber' 'CheckDigit': current_Body?.Container?.ContainerCheckDigit,util.substring(current_Body?.Container?.ContainerNumber,1,10)
                        }
                        if(current_Body?.Container?.CarrCntrSizeType){
                            'ISOSizeType'  current_Body?.Container?.CarrCntrSizeType
                        }

                        'CarrierSizeType'
                        if(current_Body?.Container?.RailPickupNumber){
                            'ContainerPickupNumber'  current_Body?.Container?.RailPickupNumber
                        }
                        'TrafficMode'{
                            'Description'
                            'OutBound'
                            'InBound'
                        }

                    }
                    current_Body?.Container?.Seal?.findAll {util.isNotEmpty(it?.SealNumber)}?.each {current_seal->
//                        def sealType = null
//                        if(current_seal?.SealType){
//                            sealType =  current_seal?.SealType
//                        }
                        'SealNumber' 'Type' : current_seal?.SealType, util.substring(current_seal?.SealNumber,1,12)
                    }

                }
                //Booking info mapping
                current_Body?.BookingGeneralInfo?.findAll {util.isNotEmpty(it?.CarrierBookingNumber)}?.each {current_BKInfo->
                    'BookingInfo'{
                        'BookingNumber' current_BKInfo?.CarrierBookingNumber
                        if(current_BKInfo?.Haulage?.InBound || current_BKInfo?.Haulage?.OutBound ){
                            'Haulage'{
                                'Outbound' ctUtil.emptyToString(current_BKInfo?.Haulage?.OutBound)
                                'Inbound'ctUtil.emptyToString(current_BKInfo?.Haulage?.InBound)
                            }
                        }
                        if(current_BKInfo?.Packaging?.PackageQty && current_BKInfo?.Packaging?.PackageQty?.toBigDecimal()>0){
                            'NumberofPackage' current_BKInfo?.Packaging?.PackageQty
                        }
                        if(current_BKInfo?.Packaging?.PackageType){
                            'PackageType'    current_BKInfo?.Packaging?.PackageType
                        }
                        if(current_BKInfo?.BookingOffice){
                            'BookingOffice' current_BKInfo?.BookingOffice
                        }
                    }
                }
                //BLInfo mapping
                current_Body?.BLGeneralInfo?.findAll {util.isNotEmpty(it?.BLNumber)}?.each {current_BLInfo->

                    'BLInfo'{
                        'BillofladingNumber' current_BLInfo.BLNumber
                        if(current_BLInfo?.BLIssueDT?.LocDT){
                            'BLIssueDate' util.convertXmlDateTime(current_BLInfo?.BLIssueDT?.LocDT,'yyyyMMddHHmmss')
                        }

                        if(current_BLInfo?.Haulage?.InBound || current_BLInfo?.Haulage?.OutBound ){
                            'Haulage'{
                                'Outbound' ctUtil.emptyToString(current_BLInfo?.Haulage?.OutBound)
                                'Inbound'ctUtil.emptyToString(current_BLInfo?.Haulage?.InBound)
                            }
                        }
                        if(current_BLInfo?.Packaging?.PackageQty && current_BLInfo?.Packaging?.PackageQty?.toBigDecimal()>0){
                            'NumberofPackage' current_BLInfo?.Packaging?.PackageQty
                        }

                        if(current_BLInfo?.Packaging?.PackageType){
                            'PackageType'  current_BLInfo?.Packaging?.PackageType
                        }
                        if(current_BLInfo?.CustomsReferenceType){
                            'CustomsReferenceType'current_BLInfo?.CustomsReferenceType
                        }
                        if(current_BLInfo?.CustomsReferenceNumber){
                            'CustomsReferenceNumber' current_BLInfo?.CustomsReferenceNumber
                        }
                        if(current_BLInfo?.CustomsClearanceLocationCode){
                            'CustomsClearanceLocationCode' current_BLInfo?.CustomsClearanceLocationCode
                        }


                    }
                }
                //Routing mapping
                POR por = current_Body?.Route?.POR
                FirstPOL firstPOL = current_Body?.Route?.FirstPOL
                LastPOD lastPOD = current_Body?.Route?.LastPOD
                FND fnd = current_Body?.Route?.FND
                'RouteInformation' ('null':'true'){

                    //#1 POR info
                    'Locations' ('null':'true'){
                        'FunctionCode' 'POR'
                        if(por?.CityDetails?.City){
                            'LocationName' util.substring(por?.CityDetails?.City,1,35)
                        }

                        'LocationDetails' ('null':'true'){
                            'LocationCodes'('null':'true'){

                                'LocationCode'
                                'MutuallyDefinedCode'
                                if (por?.CityDetails?.LocationCode?.UNLocationCode){

                                    'UNLocationCode' por?.CityDetails?.LocationCode?.UNLocationCode
                                }
                                if (por?.CityDetails?.LocationCode?.SchedKDCode){
                                    'SchedKDCode' 'Type':por?.CityDetails?.LocationCode?.SchedKDType,por?.CityDetails?.LocationCode?.SchedKDCode
                                }

                            }
                            if(por?.CityDetails?.City){
                                'CityName' por?.CityDetails?.City
                            }
                            if(por?.CSStandardCity?.CSStateCode){
                                'StateProvinceCode'  util.substring(por?.CSStandardCity?.CSStateCode,1,2)
                            }
                            if(por?.CityDetails?.State){
                                'StateProvince' por?.CityDetails?.State
                            }
                            if(por?.CityDetails?.County){
                                'County' por?.CityDetails?.County
                            }
                            if(por?.CSStandardCity?.CSCountryCode){
                                'CountryCode'   por?.CSStandardCity?.CSCountryCode
                            }
                            if(por?.CityDetails?.Country){
                                'CountryName' por?.CityDetails?.Country
                            }
                        }
                    }
                    //#2 POL info
                    'Locations' ('null':'true'){
                        'FunctionCode' 'POL'

                        if(firstPOL?.Port?.PortName){
                            'LocationName'  util.substring(firstPOL?.Port?.PortName,1,35)
                        }

                        'LocationDetails' ('null':'true'){
                            'LocationCodes' ('null':'true'){
                                'LocationCode'
                                'MutuallyDefinedCode'
                                if (firstPOL?.Port?.LocationCode?.UNLocationCode){

                                    'UNLocationCode' firstPOL?.Port?.LocationCode?.UNLocationCode
                                }
                                if (firstPOL?.Port?.LocationCode?.SchedKDCode){
                                    'SchedKDCode' 'Type':firstPOL?.Port?.LocationCode?.SchedKDType,firstPOL?.Port?.LocationCode?.SchedKDCode
                                }
                            }
                            if(firstPOL?.Port?.City){
                                'CityName'  firstPOL?.Port?.City
                            }
                            if(firstPOL?.CSStateCode){
                                'StateProvinceCode'  util.substring(firstPOL?.CSStateCode,1,2)
                            }
                            if(firstPOL?.Port?.State){
                                'StateProvince'  firstPOL?.Port?.State
                            }
                            if(firstPOL?.Port?.County){
                                'County'  firstPOL?.Port?.County
                            }
                            if(firstPOL?.Port?.CSCountryCode){
                                'CountryCode'  firstPOL?.Port?.CSCountryCode
                            }
                            if(firstPOL?.Port?.Country){
                                'CountryName'  firstPOL?.Port?.Country
                            }

                        }
                    }
                    //#3 POD info
                    'Locations' ('null':'true'){
                        'FunctionCode' 'POD'
                        if(lastPOD?.Port?.PortName){
                            'LocationName' util.substring(lastPOD?.Port?.PortName,1,35)
                        }

                        'LocationDetails' ('null':'true'){
                            'LocationCodes'  ('null':'true'){
                                'LocationCode'
                                'MutuallyDefinedCode'
                                if (lastPOD?.Port?.LocationCode?.UNLocationCode){

                                    'UNLocationCode' lastPOD?.Port?.LocationCode?.UNLocationCode
                                }
                                if (lastPOD?.Port?.LocationCode?.SchedKDCode){
                                    'SchedKDCode' 'Type':lastPOD?.Port?.LocationCode?.SchedKDType,lastPOD?.Port?.LocationCode?.SchedKDCode
                                }
                            }
                            if(lastPOD?.Port?.City){
                                'CityName' lastPOD?.Port?.City
                            }
                            if(lastPOD?.CSStateCode){
                                'StateProvinceCode' util.substring(lastPOD?.CSStateCode,1,2)
                            }
                            if(lastPOD?.Port?.State){
                                'StateProvince' lastPOD?.Port?.State
                            }
                            if(lastPOD?.Port?.County){
                                'County'  lastPOD?.Port?.County
                            }
                            if(lastPOD?.Port?.CSCountryCode){

                                'CountryCode'   lastPOD?.Port?.CSCountryCode
                            }
                            if(lastPOD?.Port?.Country){
                                'CountryName'  lastPOD?.Port?.Country
                            }

                        }
                    }
                    //#4 FND info
                    'Locations' ('null':'true'){
                        'FunctionCode' 'FND'
                        if(fnd?.CityDetails?.City){
                            'LocationName' util.substring(fnd?.CityDetails?.City,1,35)
                        }

                        'LocationDetails' ('null':'true'){
                            'LocationCodes'('null':'true'){
                                'LocationCode'
                                'MutuallyDefinedCode'
                                if (fnd?.CityDetails?.LocationCode?.UNLocationCode){

                                    'UNLocationCode' fnd?.CityDetails?.LocationCode?.UNLocationCode
                                }
                                if (fnd?.CityDetails?.LocationCode?.SchedKDCode){
                                    'SchedKDCode' 'Type':fnd?.CityDetails?.LocationCode?.SchedKDType,fnd?.CityDetails?.LocationCode?.SchedKDCode
                                }
                            }
                            if(fnd?.CityDetails?.City){
                                'CityName' fnd?.CityDetails?.City
                            }
                            if(fnd?.CSStandardCity?.CSStateCode){

                                'StateProvinceCode'  util.substring(fnd?.CSStandardCity?.CSStateCode,1,2)
                            }
                            if(fnd?.CityDetails?.State){
                                'StateProvince'   fnd?.CityDetails?.State
                            }
                            if(fnd?.CityDetails?.County){
                                'County'  fnd?.CityDetails?.County
                            }
                            if(fnd?.CSStandardCity?.CSCountryCode){
                                'CountryCode' fnd?.CSStandardCity?.CSCountryCode
                            }
                            if(fnd?.CityDetails?.Country){
                                'CountryName'  fnd?.CityDetails?.Country
                            }
                        }
                    }
                    //Route/FullPickUpDT mapping
                    LocDT fullPickDTM = null
                    def fullPickDTTimeZone = null
                    def fullPickDTisACT = null
                    if(current_Body?.Route?.FullPickupDT?.find{it?.attr_Indicator=='A'}?.LocDT){
                        fullPickDTM  = current_Body?.Route?.FullPickupDT?.find{it?.attr_Indicator=='A'}?.LocDT
                        if(fullPickDTM?.attr_TimeZone){
                            fullPickDTTimeZone = fullPickDTM.attr_TimeZone
                        }else if(fullPickDTM?.attr_CSTimeZone){
                            fullPickDTTimeZone = fullPickDTM.attr_CSTimeZone
                        }
                        fullPickDTisACT ='1'
                    }else if(current_Body?.Route?.FullPickupDT?.find{it?.attr_Indicator=='E'}?.LocDT){
                        fullPickDTM  = current_Body?.Route?.FullPickupDT?.find{it?.attr_Indicator=='E'}?.LocDT
                        if(fullPickDTM?.attr_TimeZone){
                            fullPickDTTimeZone = fullPickDTM.attr_TimeZone
                        }else if(fullPickDTM?.attr_CSTimeZone){
                            fullPickDTTimeZone = fullPickDTM.attr_CSTimeZone
                        }
                        fullPickDTisACT ='0'
                    }

                    //Route/FullRetuenCuffof Date  mapping
                    LocDT fullReturnCuffOfDTM = null
                    def fullReturnCuffOfTimeZone = null
                    if(current_Body?.Route?.FullReturnCutoffDT?.find {it?.attr_Indicator=='A'}?.LocDT){
                        fullReturnCuffOfDTM = current_Body?.Route?.FullReturnCutoffDT?.find {it?.attr_Indicator=='A'}?.LocDT
                        if(fullReturnCuffOfDTM?.attr_TimeZone){
                            fullReturnCuffOfTimeZone = fullReturnCuffOfDTM.attr_TimeZone
                        }else if(fullReturnCuffOfDTM?.attr_CSTimeZone){
                            fullReturnCuffOfTimeZone = fullReturnCuffOfDTM.attr_CSTimeZone
                        }
                    }

                    //Route/CargoReceipt Date mapping
                    LocDT cargoReceiptDTM =null
                    def cargoReceiptTimeZone = null
                    if(current_Body?.Route?.CargoReceiptDT?.find {it?.attr_Indicator =='A'}?.LocDT){
                        cargoReceiptDTM = current_Body?.Route?.CargoReceiptDT?.find {it?.attr_Indicator =='A'}?.LocDT
                        if(cargoReceiptDTM?.attr_TimeZone){
                            cargoReceiptTimeZone = cargoReceiptDTM.attr_TimeZone
                        }else if(cargoReceiptDTM?.attr_CSTimeZone){
                            cargoReceiptTimeZone = cargoReceiptDTM.attr_CSTimeZone
                        }

                    }
                    // map the departure DTM from first pol

                    LocDT departurefromPOLDTM = null
                    def departurefromPOLTimeZone = null
                    def departurefromPOLisAct = null
                    if(firstOceanLeg?.POL?.DepartureDT?.find {it?.attr_Indicator=='A'}?.LocDT){
                        departurefromPOLDTM = firstOceanLeg?.POL?.DepartureDT?.find {it?.attr_Indicator=='A'}?.LocDT
                        if(departurefromPOLDTM?.attr_TimeZone){
                            departurefromPOLTimeZone = departurefromPOLDTM.attr_TimeZone
                        }else if(departurefromPOLDTM?.attr_CSTimeZone){
                            departurefromPOLTimeZone = departurefromPOLDTM.attr_CSTimeZone
                        }
                        departurefromPOLisAct = '1'
                    }else if(firstOceanLeg?.POL?.DepartureDT?.find {it?.attr_Indicator=='E'}?.LocDT){
                        departurefromPOLDTM = firstOceanLeg?.POL?.DepartureDT?.find {it?.attr_Indicator=='E'}?.LocDT
                        if(departurefromPOLDTM?.attr_TimeZone){
                            departurefromPOLTimeZone = departurefromPOLDTM.attr_TimeZone
                        }else if(departurefromPOLDTM?.attr_CSTimeZone){
                            departurefromPOLTimeZone = departurefromPOLDTM.attr_CSTimeZone
                        }

                        departurefromPOLisAct = '0'
                    }

                    // map Arrival DTM from LastPOD

                    LocDT arrivalDTMfromPODDTM = null
                    def arrivalDTMfromPODTimeZone= null
                    def arrivalDTMfromPODisAct = null
                    if(lastOcaenLeg?.POD?.ArrivalDT?.find {it?.attr_Indicator=='A'}?.LocDT){
                        arrivalDTMfromPODDTM = lastOcaenLeg?.POD?.ArrivalDT?.find {it?.attr_Indicator=='A'}?.LocDT
                        if(arrivalDTMfromPODDTM?.attr_TimeZone){
                            arrivalDTMfromPODTimeZone = arrivalDTMfromPODDTM.attr_TimeZone
                        }else if(arrivalDTMfromPODDTM?.attr_CSTimeZone){
                            arrivalDTMfromPODTimeZone = arrivalDTMfromPODDTM.attr_CSTimeZone
                        }
                        arrivalDTMfromPODisAct = '1'
                    }else if(lastOcaenLeg?.POD?.ArrivalDT?.find {it?.attr_Indicator=='E'}?.LocDT){
                        arrivalDTMfromPODDTM = lastOcaenLeg?.POD?.ArrivalDT?.find {it?.attr_Indicator=='E'}?.LocDT
                        if(arrivalDTMfromPODDTM?.attr_TimeZone){
                            arrivalDTMfromPODTimeZone = arrivalDTMfromPODDTM.attr_TimeZone
                        }else if(arrivalDTMfromPODDTM?.attr_CSTimeZone){
                            arrivalDTMfromPODTimeZone = arrivalDTMfromPODDTM.attr_CSTimeZone
                        }
                        arrivalDTMfromPODisAct = '0'
                    }
                    //map Arrival DTM from FND

                    LocDT arrivalAtFinalHubDTM = null
                    def arrivalAtFinalHubTimeZone= null
                    def arrivalAtFinalHubisAct = null
                    if(fnd?.ArrivalDT?.find {it?.attr_Indicator=='A'}?.LocDT){
                        arrivalAtFinalHubDTM = fnd?.ArrivalDT?.find {it?.attr_Indicator=='A'}?.LocDT
                        if(arrivalAtFinalHubDTM?.attr_TimeZone){
                            arrivalAtFinalHubTimeZone = arrivalAtFinalHubDTM.attr_TimeZone
                        }else if(arrivalAtFinalHubDTM?.attr_CSTimeZone){
                            arrivalAtFinalHubTimeZone = arrivalAtFinalHubDTM.attr_CSTimeZone
                        }
                        arrivalAtFinalHubisAct = '1'
                    }else if(fnd?.ArrivalDT?.find {it?.attr_Indicator=='E'}?.LocDT) {
                        arrivalAtFinalHubDTM = fnd?.ArrivalDT?.find { it?.attr_Indicator == 'E' }?.LocDT
                        if (arrivalAtFinalHubDTM?.attr_TimeZone) {
                            arrivalAtFinalHubTimeZone = arrivalAtFinalHubDTM.attr_TimeZone
                        } else if (arrivalAtFinalHubDTM?.attr_CSTimeZone) {
                            arrivalAtFinalHubTimeZone = arrivalAtFinalHubDTM.attr_CSTimeZone
                        }
                        arrivalAtFinalHubisAct = '0'
                    }
                    //map from CargoDeliveryDT
                    LocDT cargoDeliveryDTM =null
                    def cargoDeliveryTimeZone = null
                    def cargoDeliveryisAct = null
                    if(current_Body?.Route?.CargoDeliveryDT?.find {it?.attr_Indicator =='A'}?.LocDT){
                        cargoDeliveryDTM = current_Body?.Route?.CargoDeliveryDT?.find {it?.attr_Indicator =='A'}?.LocDT
                        if(cargoDeliveryDTM?.attr_TimeZone){
                            cargoDeliveryTimeZone = cargoDeliveryDTM.attr_TimeZone
                        }else if(cargoDeliveryDTM?.attr_CSTimeZone){
                            cargoDeliveryTimeZone = cargoDeliveryDTM.attr_CSTimeZone
                        }
                        cargoDeliveryisAct ='1'
                    }else if(current_Body?.Route?.CargoDeliveryDT?.find {it?.attr_Indicator =='E'}?.LocDT){
                        cargoDeliveryDTM = current_Body?.Route?.CargoDeliveryDT?.find {it?.attr_Indicator =='E'}?.LocDT
                        if(cargoDeliveryDTM?.attr_TimeZone){
                            cargoDeliveryTimeZone = cargoDeliveryDTM.attr_TimeZone
                        }else if(cargoDeliveryDTM?.attr_CSTimeZone){
                            cargoDeliveryTimeZone = cargoDeliveryDTM.attr_CSTimeZone
                        }
                        cargoDeliveryisAct ='0'
                    }

                    'EventDate'{
                        if(fullPickDTM){
                            'FullPickup'  'EstActIndicator':fullPickDTisACT,'TimeZone':fullPickDTTimeZone, util.convertXmlDateTime(fullPickDTM,'yyyyMMddHHmmss')
                        }
                        if(fullReturnCuffOfDTM){
                            'FullReturnCutoff' 'EstActIndicator': '1','TimeZone' : fullReturnCuffOfTimeZone,util.convertXmlDateTime(fullReturnCuffOfDTM,'yyyyMMddHHmmss')
                        }
                        if(cargoReceiptDTM){
                            'CargoReceipt' 'EstActIndicator': '1','TimeZone' : cargoReceiptTimeZone,util.convertXmlDateTime(cargoReceiptDTM,'yyyyMMddHHmmss')
                        }
                        if(departurefromPOLDTM){
                            'DepartureFromFirstPOL'  'EstActIndicator':departurefromPOLisAct,'TimeZone':departurefromPOLTimeZone, util.convertXmlDateTime(departurefromPOLDTM,'yyyyMMddHHmmss')
                        }

                        if(arrivalDTMfromPODDTM){
                            'ArrivalAtLastPOD'  'EstActIndicator':arrivalDTMfromPODisAct,'TimeZone':arrivalDTMfromPODTimeZone, util.convertXmlDateTime(arrivalDTMfromPODDTM,'yyyyMMddHHmmss')
                        }
                        if(arrivalAtFinalHubDTM){
                            'ArrivalAtFinalHub' 'EstActIndicator':arrivalAtFinalHubisAct,'TimeZone':arrivalAtFinalHubTimeZone, util.convertXmlDateTime(arrivalAtFinalHubDTM,'yyyyMMddHHmmss')
                        }

                        if(cargoDeliveryDTM){
                            'CargoDelivery'   'EstActIndicator':cargoDeliveryisAct,'TimeZone':cargoDeliveryTimeZone, util.convertXmlDateTime(cargoDeliveryDTM,'yyyyMMddHHmmss')
                        }

                    }
                }

                def limitedVesselVoyeageInfo = 10 //VesselVoyageInformation max occus : 10

                'VesselVoyageInformation' ('null':'true'){
                    // loop by oceanleg
                    current_Body?.Route?.OceanLeg?.each {current_occeanleg ->
                        if(limitedVesselVoyeageInfo>0){
                            // port details for POL
                            'PortDetails'{
                                'Port'{
                                    'FunctionCode' 'POL'
                                    if(current_occeanleg?.POL?.Port?.PortName){
                                        'LocationName' util.substring(current_occeanleg?.POL?.Port?.PortName,1,35)
                                    }
                                    'LocationDetails' ('null':'true'){
                                        'LocationCodes'('null':'true'){
                                            'LocationCode'
                                            'MutuallyDefinedCode'
                                            if(current_occeanleg?.POL?.Port?.LocationCode?.UNLocationCode){
                                                'UNLocationCode' current_occeanleg?.POL?.Port?.LocationCode?.UNLocationCode
                                            }
                                            if(current_occeanleg?.POL?.Port?.LocationCode?.SchedKDCode){
                                                'SchedKDCode' 'Type':current_occeanleg?.POL?.Port?.LocationCode?.SchedKDType,  current_occeanleg?.POL?.Port?.LocationCode?.SchedKDCode
                                            }


                                        }
                                        if(current_occeanleg?.POL?.Port?.City){
                                            'CityName'  current_occeanleg?.POL?.Port?.City
                                        }
                                        if(current_occeanleg?.POL?.CSStateCode){
                                            'StateProvinceCode' util.substring(current_occeanleg?.POL?.CSStateCode,1,2)
                                        }
                                        if(current_occeanleg?.POL?.Port?.State){
                                            'StateProvince'  current_occeanleg?.POL?.Port?.State
                                        }
                                        if(current_occeanleg?.POL?.Port?.County){
                                            'County'   current_occeanleg?.POL?.Port?.County
                                        }
                                        if(current_occeanleg?.POL?.Port?.CSCountryCode){
                                            'CountryCode' current_occeanleg?.POL?.Port?.CSCountryCode
                                        }
                                        if(current_occeanleg?.POL?.Port?.Country){
                                            'CountryName' current_occeanleg?.POL?.Port?.Country
                                        }
                                    }

                                }
                                if(current_occeanleg?.SVVD?.Loading?.Service || current_occeanleg?.SVVD?.Loading?.Voyage ||current_occeanleg?.SVVD?.Loading?.Vessel ||current_occeanleg?.SVVD?.Loading?.VesselName ||current_occeanleg?.SVVD?.Loading?.RegistrationCountry ){
                                    'VesselVoyage'{
                                        'VoyageEvent' 'Loading'
                                        if(current_occeanleg?.SVVD?.Loading?.Service){
                                            'ServiceName' 'Code':util.substring(current_occeanleg?.SVVD?.Loading?.Service,1,4)
                                        }

                                        if(current_occeanleg?.SVVD?.Loading?.Voyage || current_occeanleg?.SVVD?.Loading?.Direction ){
                                            'VoyageNumberDirection' util.substring(ctUtil.emptyToString(current_occeanleg?.SVVD?.Loading?.Voyage)+ctUtil.emptyToString(current_occeanleg?.SVVD?.Loading?.Direction),1,22)
                                        }
                                        if(current_occeanleg?.SVVD?.Loading?.Vessel||current_occeanleg?.SVVD?.Loading?.VesselName||current_occeanleg?.SVVD?.Loading?.RegistrationCountry){

                                            'VesselInformation'{
                                                if(current_occeanleg?.SVVD?.Loading?.Vessel){
                                                    def lloydsCode = null
                                                    def callSign = null
                                                    if(current_occeanleg?.SVVD?.Loading?.LloydsNumber){
                                                        lloydsCode = current_occeanleg?.SVVD?.Loading?.LloydsNumber
                                                    }
                                                    if(current_occeanleg?.SVVD?.Loading?.CallSign){
                                                        callSign = current_occeanleg?.SVVD?.Loading?.CallSign
                                                    }
                                                    'VesselCode'  'LloydsCode':lloydsCode,'CallSign':callSign ,util.substring(current_occeanleg?.SVVD?.Loading?.Vessel,1,10)
                                                }
                                                if(current_occeanleg?.SVVD?.Loading?.VesselName){
                                                    'VesselName' util.substring(current_occeanleg?.SVVD?.Loading?.VesselName,1,30)
                                                }
                                                if(current_occeanleg?.SVVD?.Loading?.RegistrationCountry){
                                                    'VesselRegistrationCountry' util.substring(current_occeanleg?.SVVD?.Loading?.RegistrationCountry,1,35)
                                                }

                                            }
                                        }

                                    }
                                }
                                LocDT EventDTMforPOL = null
                                def EventDTMforPOLTimeZone = null
                                def EventDTMforPOLisAct = null

                                if(current_occeanleg?.POL?.DepartureDT?.find {it?.attr_Indicator=='A'}?.LocDT){
                                    EventDTMforPOL = current_occeanleg?.POL?.DepartureDT?.find {it?.attr_Indicator=='A'}?.LocDT
                                    if(EventDTMforPOL?.attr_TimeZone){
                                        EventDTMforPOLTimeZone = EventDTMforPOL.attr_TimeZone
                                    }else if(EventDTMforPOL?.attr_CSTimeZone){
                                        EventDTMforPOLTimeZone = EventDTMforPOL.attr_CSTimeZone
                                    }
                                    EventDTMforPOLisAct = '1'
                                }else if(current_occeanleg?.POL?.DepartureDT?.find {it?.attr_Indicator=='E'}?.LocDT){
                                    EventDTMforPOL = current_occeanleg?.POL?.DepartureDT?.find {it?.attr_Indicator=='E'}?.LocDT
                                    if(EventDTMforPOL?.attr_TimeZone){
                                        EventDTMforPOLTimeZone = EventDTMforPOL.attr_TimeZone
                                    }else if(EventDTMforPOL?.attr_CSTimeZone){
                                        EventDTMforPOLTimeZone = EventDTMforPOL.attr_CSTimeZone
                                    }
                                    EventDTMforPOLisAct = '0'

                                }
                                if(EventDTMforPOL){
                                    'EventDate'{
                                        'Departure' 'EstActIndicator':EventDTMforPOLisAct,'TimeZone':EventDTMforPOLTimeZone, util.convertXmlDateTime(EventDTMforPOL,'yyyyMMddHHmmss')
                                    }

                                }
                                if(current_occeanleg?.LegSeq){
                                    'LegSequence' current_occeanleg?.LegSeq
                                }

                            }
                            // port details for POD
                            'PortDetails'{
                                'Port'{
                                    'FunctionCode' 'POD'
                                    if(current_occeanleg?.POD?.Port?.PortName){
                                        'LocationName' util.substring(current_occeanleg?.POD?.Port?.PortName,1,35)
                                    }
                                    'LocationDetails' ('null':'true'){
                                        'LocationCodes'('null':'true'){
                                            'LocationCode'
                                            'MutuallyDefinedCode'
                                            if(current_occeanleg?.POD?.Port?.LocationCode?.UNLocationCode){
                                                'UNLocationCode' current_occeanleg?.POD?.Port?.LocationCode?.UNLocationCode
                                            }
                                            if(current_occeanleg?.POD?.Port?.LocationCode?.SchedKDCode){
                                                'SchedKDCode' 'Type':current_occeanleg?.POD?.Port?.LocationCode?.SchedKDType, current_occeanleg?.POD?.Port?.LocationCode?.SchedKDCode
                                            }
                                        }
                                        if(current_occeanleg?.POD?.Port?.City){
                                            'CityName'  current_occeanleg?.POD?.Port?.City
                                        }
                                        if(current_occeanleg?.POD?.CSStateCode){
                                            'StateProvinceCode' util.substring(current_occeanleg?.POD?.CSStateCode,1,2)
                                        }
                                        if(current_occeanleg?.POD?.Port?.State){
                                            'StateProvince'  current_occeanleg?.POD?.Port?.State
                                        }
                                        if(current_occeanleg?.POD?.Port?.County){
                                            'County'   current_occeanleg?.POD?.Port?.County
                                        }
                                        if(current_occeanleg?.POD?.Port?.CSCountryCode){
                                            'CountryCode' current_occeanleg?.POD?.Port?.CSCountryCode
                                        }
                                        if(current_occeanleg?.POD?.Port?.Country){
                                            'CountryName' current_occeanleg?.POD?.Port?.Country
                                        }
                                    }

                                }
                                if(current_occeanleg?.SVVD?.Discharge?.Service || current_occeanleg?.SVVD?.Discharge?.Voyage ||current_occeanleg?.SVVD?.Discharge?.Vessel ||current_occeanleg?.SVVD?.Discharge?.VesselName ||current_occeanleg?.SVVD?.Discharge?.RegistrationCountry ){
                                    'VesselVoyage'{
                                        'VoyageEvent' 'Discharge'
                                        if(current_occeanleg?.SVVD?.Discharge?.Service){
                                            'ServiceName' 'Code':util.substring(current_occeanleg?.SVVD?.Discharge?.Service,1,4)
                                        }

                                        if(current_occeanleg?.SVVD?.Discharge?.Voyage || current_occeanleg?.SVVD?.Discharge?.Direction ){
                                            'VoyageNumberDirection' util.substring(ctUtil.emptyToString(current_occeanleg?.SVVD?.Discharge?.Voyage)+ctUtil.emptyToString(current_occeanleg?.SVVD?.Discharge?.Direction),1,22)
                                        }
                                        if(current_occeanleg?.SVVD?.Discharge?.Vessel||current_occeanleg?.SVVD?.Discharge?.VesselName||current_occeanleg?.SVVD?.Discharge?.RegistrationCountry){

                                            'VesselInformation'{
                                                if(current_occeanleg?.SVVD?.Discharge?.Vessel){
                                                    def lloydsCode = null
                                                    def callSign = null
                                                    if(current_occeanleg?.SVVD?.Discharge?.LloydsNumber){
                                                        lloydsCode = current_occeanleg?.SVVD?.Discharge?.LloydsNumber
                                                    }
                                                    if(current_occeanleg?.SVVD?.Discharge?.CallSign){
                                                        callSign = current_occeanleg?.SVVD?.Discharge?.CallSign
                                                    }
                                                    'VesselCode'  'LloydsCode':lloydsCode,'CallSign':callSign ,util.substring(current_occeanleg?.SVVD?.Discharge?.Vessel,1,10)
                                                }
                                                if(current_occeanleg?.SVVD?.Discharge?.VesselName){
                                                    'VesselName' util.substring(current_occeanleg?.SVVD?.Discharge?.VesselName,1,30)
                                                }
                                                if(current_occeanleg?.SVVD?.Discharge?.RegistrationCountry){
                                                    'VesselRegistrationCountry' util.substring(current_occeanleg?.SVVD?.Discharge?.RegistrationCountry,1,35)
                                                }

                                            }
                                        }

                                    }
                                }
                                LocDT EventDTMforPOD = null
                                def EventDTMforPODTimeZone = null
                                def EventDTMforPODisAct = null

                                if(current_occeanleg?.POD?.ArrivalDT?.find {it?.attr_Indicator=='A'}?.LocDT){
                                    EventDTMforPOD = current_occeanleg?.POD?.ArrivalDT?.find {it?.attr_Indicator=='A'}?.LocDT
                                    if(EventDTMforPOD?.attr_TimeZone){
                                        EventDTMforPODTimeZone = EventDTMforPOD.attr_TimeZone
                                    }else if(EventDTMforPOD?.attr_CSTimeZone){
                                        EventDTMforPODTimeZone = EventDTMforPOD.attr_CSTimeZone
                                    }
                                    EventDTMforPODisAct = '1'
                                }else if(current_occeanleg?.POD?.ArrivalDT?.find {it?.attr_Indicator=='E'}?.LocDT){
                                    EventDTMforPOD = current_occeanleg?.POD?.ArrivalDT?.find {it?.attr_Indicator=='E'}?.LocDT
                                    if(EventDTMforPOD?.attr_TimeZone){
                                        EventDTMforPODTimeZone = EventDTMforPOD.attr_TimeZone
                                    }else if(EventDTMforPOD?.attr_CSTimeZone){
                                        EventDTMforPODTimeZone = EventDTMforPOD.attr_CSTimeZone
                                    }
                                    EventDTMforPODisAct = '0'

                                }
                                if(EventDTMforPOD){
                                    'EventDate'{
                                        'Arrival' 'EstActIndicator':EventDTMforPODisAct,'TimeZone':EventDTMforPODTimeZone, util.convertXmlDateTime(EventDTMforPOD,'yyyyMMddHHmmss')
                                    }

                                }
                                if(current_occeanleg?.LegSeq){
                                    'LegSequence' current_occeanleg?.LegSeq
                                }

                            }
                        }
                        limitedVesselVoyeageInfo--
                    }

                }
                //loop External Reference
                def externalRefMap = ['BKG':'BN','BL':'BM','CR':'CR','CTR':'CT','EXPR':'RF','FCBL':'FCN','FI':'FI','FR':'FN','INV':'IK','PO':'PO','PR':'Q1','SC':'E8','SID':'SI','SO':'SO','SR':'SR','TARIF':'TS','WARG':'QT']


                current_Body?.ExternalReference?.findAll {externalRefMap.containsKey(it?.CSReferenceType)}each {current_ExtRef->
                    'ExternalReferences'{
                        'ReferenceType' externalRefMap.get(current_ExtRef?.CSReferenceType)
                        'ReferenceNumber' util.substring(current_ExtRef?.ReferenceNumber,1,35)
                    }
                }
                //loop Party info without party level
                def partyTypeMap = ['ANP':'AN','BPT':'BP','CGN':'CN','FWD':'FW','NPT':'NP','OTH':'UK','SHP':'SH','UK':'UK']
                current_Body?.Party?.findAll{partyTypeMap.containsKey(it?.PartyType)}?.each {current_Party ->
                    if((["OOLU","COSU"].contains(current_Body?.GeneralInformation?.SCAC) && current_Party?.PartyLevel) || (current_Body?.GeneralInformation?.SCAC == "COSU" && current_Party?.PartyType == "BK") ){

                    } else {
                        'LegalParties'{
                            'CustomerType' partyTypeMap.get(current_Party?.PartyType)
                            if(current_Party?.PartyName){
                                'CustomerName' util.substring(current_Party?.PartyName,1,70)
                            }
                            if(current_Party?.CarrierCustomerCode){
                                'CustomerCode' util.substring(current_Party?.CarrierCustomerCode,1,10)
                            }
                        }
                    }

                }
                //loop by Body/Cargo
                current_Body?.Cargo?.eachWithIndex{ current_Cargo, index ->
                    'CargoInfo'{
                        'Cargo'{
                            'ItemNo' index+1
                            'CargoDescription'
                        }
                        // loop by marks and number

                            'MarksAndNo'{
                                current_Cargo?.MarksAndNumbers?.each {current_Marks->
                                    'MarksandNumbers' current_Marks?.MarksAndNumbersLine
                                }
                        }
                    }
                }


            }

            // SummaryDetails mapping
            'SummaryDetails'{
                'OtherRemarks'{
                    'RemarksLines'
                }
                //loop by mile staones

                current_Body?.MileStones?.each {current_MilesStones ->
                    def mlesStoneEventConversion =  util.getConversion('CARDINALMARITIME', 'CT', 'O', 'EventCodeType', current_MilesStones?.CS1Event, conn)
                    if(mlesStoneEventConversion){
                        'OtherMilestones'{
                            'MileStoneCode'  mlesStoneEventConversion
                            def mileStoneDTMTimeZone = null
                            if(current_MilesStones?.EventDT?.LocDT){
                                LocDT  mileStoneDate =current_MilesStones?.EventDT?.LocDT
                                if(mileStoneDate?.attr_TimeZone){
                                    mileStoneDTMTimeZone = mileStoneDate?.attr_TimeZone
                                }else if(mileStoneDate?.attr_CSTimeZone){
                                    mileStoneDTMTimeZone = mileStoneDate?.attr_CSTimeZone
                                }

                                'MileStoneDate''TimeZone': mileStoneDTMTimeZone, util.convertXmlDateTime(mileStoneDate, 'yyyyMMddHHmmss')
                            }else if(current_MilesStones?.EventDT?.GMT){
                                'MileStoneDate''TimeZone': 'GMT', util.convertXmlDateTime(current_MilesStones?.EventDT?.GMT, 'yyyyMMddHHmmss')
                            }

                            'MilestoneLocationCode'{
                                if(current_MilesStones?.Location?.LocationCode?.UNLocationCode){
                                    'LocationCode' current_MilesStones?.Location?.LocationCode?.UNLocationCode
                                }
                                'MutuallyDefinedCode'
                                'UNLocationCode'
                                'SchedKDCode'
                            }
                            if(current_MilesStones?.Location?.CityDetails?.City){
                                'MilestoneCityName' util.substring(current_MilesStones?.Location?.CityDetails?.City,1,35)
                            }

                            if(current_MilesStones?.Location?.CSStandardCity?.CSStateCode){
                                'MilestoneStateCode'  util.substring(current_MilesStones?.Location?.CSStandardCity?.CSStateCode,1,2)
                            }
                            if(current_MilesStones?.Location?.CSStandardCity?.CSCountryCode){
                                'MilestoneCountryCode' util.substring(current_MilesStones?.Location?.CSStandardCity?.CSCountryCode,1,2)
                            }
                            if(current_MilesStones?.TransportMode){
                                'MilestoneTransportMode' util.substring(current_MilesStones?.TransportMode,1,10)
                            }
                            if(current_MilesStones?.TransportCarrier){
                                'MilestoneTransportCarrier' util.substring(current_MilesStones?.TransportCarrier,1,35)
                            }
                        }
                    }

                }


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
       // def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false)); //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print

        def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
        outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
        outXml.omitEmptyAttributes = true
        outXml.omitNullAttributes = true



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
        def CargoTrackingXML = outXml.createNode('CargoTrackingXML')
        def bizKeyRoot = bizKeyXml.createNode('root')
        def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.


        //BeginWorkFlow
        TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
        def outputBodyCount = 0
        //def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, "X.12", conn)
        def headerMsgDT = util.convertDateTime(ct.Header.MsgDT.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
        //long ediIsaCtrlNumber = ctrlNos[0]
        //long ediGroupCtrlNum = ctrlNos[1]
        def txnErrorKeys = []
        //special logic for get scac from first body

        //duplication -- CT special logic
//		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
//
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
//		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

        //start body looping
        ct.Body.eachWithIndex { current_Body, current_BodyIndex ->


            def eventCS2Cde = current_Body.Event.CS1Event
            def eventExtCde = util.getConversionWithScac('CARDINALMARITIME', 'CT', 'O', 'EventCodeType', eventCS2Cde,current_Body?.GeneralInformation?.SCAC,conn)
            //prep checking
            List<Map<String,String>> errorKeyList = prepValidation(current_Body, current_BodyIndex, eventCS2Cde, eventExtCde)

            if (errorKeyList.isEmpty()) {
                //pass validateBeforeExecution
                //main mapping
                generateBody(current_Body,ct.Header,outXml)
                outputBodyCount++
            }

            // posp checking
            if (errorKeyList.isEmpty()) {
                //scheam validation
                String validateStr = util.cleanXml(writer.toString() + "</CargoTrackingXML>",true)
                pospValidation(current_Body, validateStr, errorKeyList)
            }

//            //posp checking
//            pospValidation()

            ctUtil.buildCsupload(csuploadXml, errorKeyList, String.format('%19s', current_Body.TransactionInformation.InterchangeTransactionID)?.replace(" ", "0"), MSG_REQ_ID)
            ctUtil.buildBizKey(bizKeyXml, current_Body, current_BodyIndex, errorKeyList, headerMsgDT, eventExtCde, TP_ID, conn)

            txnErrorKeys.add(errorKeyList);
        }

        //EndWorkFlow

        //End root node
        outXml.nodeCompleted(null,CargoTrackingXML)
        bizKeyXml.nodeCompleted(null,bizKeyRoot)
        csuploadXml.nodeCompleted(null,csuploadRoot)

        //		println bizKeyWriter.toString();
        //		println csuploadWriter.toString();

        //promote csupload and bizkey to session
        ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
        ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
        ctUtil.promoteAssoInterchangeMessageIDToSession(appSessionId,ct.Header.InterchangeMessageID);
        if(ct.Body[0]?.GeneralInformation?.SCAC)ctUtil.promoteAssoCarrierSCACToSession(appSessionId,ct.Body[0]?.GeneralInformation?.SCAC);

        String result = "";
        if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
            //if exists one txn without error, then return result
            result = writer?.toString()
            result = util.cleanXml(result,true)
            result = result.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\"?>")

            String outputFileName = "CARDINALMARITIME_CT_" + MSG_REQ_ID + ".xml"
            ctUtil.promoteOutputFileNameToSession(appSessionId, outputFileName)

        }

        writer.close();
        csuploadWriter.close()
        bizKeyWriter.close()



        return result;
    }

    public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
        List<Map<String, String>> errorKeyList = new ArrayList<Map<String, String>>();

        //missing scac
        ctUtil.missingSCAC(eventCS2Cde, current_Body, true, "Missing GeneralInfo.SCAC", errorKeyList)
        //missing event code
//        ctUtil.missingEventStatusCode(eventCS2Cde, eventExtCde, false, '-Missing \"EventCode\" in output XML', errorKeyList)

        ctUtil.missingEventDate(eventCS2Cde,current_Body?.Event?.EventDT, true,null, errorKeyList)

        //missing booking num
        ctUtil.missingBookingNumber(eventCS2Cde, current_Body?.BookingGeneralInfo, true, null, errorKeyList)
        // missing bl number
        current_Body?.BLGeneralInfo?.each { current_blInfo->
            ctUtil.missingBLNumberbutexistsBLGeninfo(eventCS2Cde, current_blInfo, true, null, errorKeyList)
        }

        //missing legal party
        List legalParytList = ['ANP', 'BPT', 'CGN', 'FWD', 'NPT', 'OTH', 'SHP', 'UK']

        List legalPartyType = current_Body?.Party?.findAll {util.isNotEmpty(it?.PartyType)}?.PartyType?.intersect(legalParytList)
        ctUtil.missingLegalParty(eventCS2Cde,legalPartyType,true,null,errorKeyList)
        //CS260 	Ignore CS260 (I/B intermodal)
        ctUtil.filterIBIntermodal(eventCS2Cde,'CS260',current_Body?.Route?.Inbound_intermodal_indicator,false,null,errorKeyList)

        return errorKeyList


    }


    void pospValidation(cs.b2b.core.mapping.bean.ct.Body current_Body, String outputXml, List<Map<String,String>> errorKeyList) {

        ctUtil.customerSchemaValidation(outputXml, 'CUS-CTXML-STD', errorKeyList)

    }


}
