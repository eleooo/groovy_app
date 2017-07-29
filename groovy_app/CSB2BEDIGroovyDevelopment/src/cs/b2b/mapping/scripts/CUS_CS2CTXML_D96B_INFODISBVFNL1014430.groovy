package cs.b2b.mapping.scripts


import cs.b2b.core.mapping.bean.ct.Seal
import cs.b2b.core.mapping.bean.ct.Body
import cs.b2b.core.mapping.bean.ct.ContainerMovement
import cs.b2b.core.mapping.bean.ct.OceanLeg
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder

import java.sql.Connection

/**
 * Created by XIAOTR on 6/20/2017.
 */
class CUS_CS2CTXML_D96B_INFODISBVFNL1014430 {


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

    def headerMsgDT = null



    public void generateBody(Body current_Body, MarkupBuilder outXml) {

        //CT special fields
        def vCS1Event = current_Body.Event.CS1Event
        def vCS1EventFirst5 = util.substring(vCS1Event,1,5)

        def SCAC = current_Body?.GeneralInformation?.SCAC

        def shipDir = util.getConversionWithoutTP('CT','O','CSSTDQ2',vCS1EventFirst5,conn)

        outXml.'Group_UNH' {
                'UNH' {
                    'E0062_01' '-999'
                    'S009_02' {
                        'E0065_01' 'IFTSTA'
                        'E0052_02' 'D'
                        'E0054_03' '96B'
                        'E0051_04' 'UN'
                        'E0057_05' ''
                    }
                }
                'BGM' {
                    'C002_01' {
                        'E1001_01' '23'
                        'E1131_02' ''
                        'E3055_03' ''
                        'E1000_04' 'IFTSTA'
                    }
                    'C106_02' {
                        if(current_Body?.TransactionInformation?.InterchangeTransactionID){
                            'E1004_01' util.substring(current_Body?.TransactionInformation?.InterchangeTransactionID,1,19)
                        }

                        'E1056_02' ''
                        'E1060_03' ''
                    }

                    'E1225_03' '9'


                    'E4343_04' ''
                }
                'DTM' {
                    if(headerMsgDT){
                        'C507_01' {
                            'E2005_01' '137'
                            'E2380_02' util.substring(headerMsgDT,1,12)
                            'E2379_03' '203'
                        }
                    }
                }
            /////////////////////////////////Group4 Start////////////////////////////////////////////////////////////////////////
                'Group4_CNI' {
                    'CNI' {
                        'E1490_01' '1'
                    }
                    /////////////////////////////////Group5 Start////////////////////////////////////////////////////////////////////////
                    'Group5_STS' {
                        'STS' {
                            'C601_01' {
                                'E9015_01' '1'
                                'E1131_02' ''
                                'E3055_03' ''
                            }

                            def queryEventConversion = util.getConversion('INFODISBVFNL1014430','CT','O','EventCdeCSIntVal',vCS1Event,conn)

                            'C555_02' {
                                if(queryEventConversion && queryEventConversion!='XX'){
                                    'E9011_01' queryEventConversion
                                }
                                'E1131_02' ''
                                'E3055_03' ''
                                'E9010_04' ''
                            }
                        }
                        //get BL Numer
                        // RFF maxOccurs="9"
                        def group5RFFLimitaion = 9
                        current_Body?.BLGeneralInfo?.findAll{util.isNotEmpty(it?.BLNumber)}?.groupBy {it?.BLNumber}?.each { BLNumber, current_BLGeneralInfo ->
                            if(group5RFFLimitaion>0){
                                'RFF' {
                                    'C506_01' {
                                        'E1153_01' 'BM'
                                        'E1154_02' util.substring(BLNumber,1,35)
                                        'E1156_03' ''
                                        'E4000_04' ''
                                    }
                                    group5RFFLimitaion--
                                }
                            }

                        }
                        //get booking bumber
                        current_Body?.BookingGeneralInfo?.findAll{util.isNotEmpty(it?.CarrierBookingNumber)}?.groupBy {it?.CarrierBookingNumber}?.each { CarrierBookingNumber,current_bkGeneralInfo ->
                            if(group5RFFLimitaion>0){
                                'RFF' {
                                    'C506_01' {
                                        'E1153_01' 'BN'
                                        'E1154_02' util.substring(CarrierBookingNumber, 1, 35)
                                        'E1156_03' ''
                                        'E4000_04' ''
                                    }
                                    group5RFFLimitaion--
                                }
                            }

                        }
                        // get container info
                        def containerInfo =null
                        if(current_Body?.Container?.ContainerNumber || current_Body?.Container?.ContainerCheckDigit){
                            containerInfo  = (current_Body?.Container?.ContainerNumber ? util.substring(current_Body?.Container?.ContainerNumber,1,10) : '') + (current_Body?.Container?.ContainerCheckDigit ? current_Body?.Container?.ContainerCheckDigit : '')
                        }

                        if(containerInfo && group5RFFLimitaion>0){
                            'RFF' {
                                'C506_01' {
                                    'E1153_01' 'EQ'
                                    'E1154_02' containerInfo
                                    'E1156_03' ''
                                    'E4000_04' ''
                                }
                                group5RFFLimitaion--
                            }
                        }


                        //get External Reference
                        def externalReferenceMap = ['FN':'FN','INV':'IN','PO':'PO','SC':'SC','SID':'SI','SO':'SO','SR':'SR','VT':'VT']
                        def individualReferenceNumer = []// if exists same ReferenceNumer ,then skip
                        current_Body?.ExternalReference?.each {current_ExtRef ->
                            if(externalReferenceMap.containsKey(current_ExtRef?.CSReferenceType) && !individualReferenceNumer.contains(util.substring(current_ExtRef?.ReferenceNumber,1,35)) && group5RFFLimitaion>0){
                                'RFF' {
                                    'C506_01' {
                                        'E1153_01' externalReferenceMap.get(current_ExtRef?.CSReferenceType)
                                        'E1154_02' util.substring(current_ExtRef?.ReferenceNumber,1,35)
                                        'E1156_03' ''
                                        'E4000_04' ''
                                    }
                                    individualReferenceNumer.add(util.substring(current_ExtRef?.ReferenceNumber,1,35))
                                    group5RFFLimitaion--
                                }
                            }
                        }


                        if(current_Body?.GeneralInformation?.SCAC && group5RFFLimitaion>0){
                            'RFF' {
                                'C506_01' {
                                    'E1153_01' 'AAZ'
                                    'E1154_02' current_Body?.GeneralInformation?.SCAC
                                    'E1156_03' ''
                                    'E4000_04' ''
                                }
                                group5RFFLimitaion--
                            }
                        }
                        'DTM' {
                            'C507_01' {
                                'E2005_01' '334'
                                if(current_Body?.Event?.EventDT?.LocDT){
                                    'E2380_02' util.convertXmlDateTime(current_Body?.Event?.EventDT?.LocDT,'yyyyMMddHHmm')
                                }

                                'E2379_03' '203'
                            }
                        }


                        //party type conversion
                        def group5NADLimitaion = 9
                        def partyTypeMap = ['ANP':'AN','BPT':'BP','CGN':'CN','FWD':'FW','NPT':'NP','OTH':'UK','SHP':'CZ']
                        current_Body?.Party?.findAll {util.isEmpty(it?.PartyLevel)}?.each{current_Party ->
                            if(partyTypeMap?.containsKey(current_Party?.PartyType) && current_Party?.CarrierCustomerCode && group5NADLimitaion>0){
                                'NAD' {
                                    'E3035_01' partyTypeMap.get(current_Party?.PartyType)
                                    'C082_02' {
                                        'E3039_01' util.substring(current_Party?.CarrierCustomerCode,1,10)
                                        'E1131_02' ''
                                        'E3055_03' ''
                                    }
                                    group5NADLimitaion--
                                }
                            }
                            
                        }

                        if(SCAC && group5NADLimitaion>0){
                            'NAD' {
                                'E3035_01' 'CA'
                                'C082_02' {
                                    'E3039_01' SCAC
                                    'E1131_02' ''
                                    'E3055_03' ''
                                }

                                group5NADLimitaion--
                            }
                        }

                        'LOC' {
                            'E3227_01' 'ZZ'
                            'C517_02' {
                                if(current_Body?.Event?.Location?.LocationCode?.UNLocationCode){
                                    'E3225_01'  util.substring(current_Body?.Event?.Location?.LocationCode?.UNLocationCode,1,5)
                                }else if(current_Body?.Event?.Location?.LocationCode?.SchedKDCode){
                                    'E3225_01'  util.substring(current_Body?.Event?.Location?.LocationCode?.SchedKDCode,1,5)
                                }

                                'E1131_02' '139'
                                if(current_Body?.Event?.Location?.LocationCode?.UNLocationCode){
                                    'E3055_03' '6'
                                }else if(current_Body?.Event?.Location?.LocationCode?.SchedKDCode){
                                    'E3055_03' '5'
                                }
                                if(current_Body?.Event?.Location?.CityDetails?.City){
                                    'E3224_04' util.substring(current_Body?.Event?.Location?.CityDetails?.City,1,60)
                                }
                            }
                            'C519_03' {
                                if(current_Body?.Event?.Location?.LocationCode?.UNLocationCode){
                                    'E3223_01'  util.substring(current_Body?.Event?.Location?.LocationCode?.UNLocationCode,1,5)
                                }else if(current_Body?.Event?.Location?.LocationCode?.SchedKDCode){
                                    'E3223_01'  util.substring(current_Body?.Event?.Location?.LocationCode?.SchedKDCode,1,5)
                                }
                                'E1131_02' '145'
                                'E3055_03' '6'
                                'E3222_04' util.substring(current_Body?.Event?.Location?.CityDetails?.City,1,60)
                            }
                            'C553_04' {
                                if(current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode){
                                    'E3233_01' util.substring(current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode,1,2)

                                }
                                'E1131_02' '162'
                                'E3055_03' '6'

                                def queryCountry = util.getCS2MasterCity4CountryNameByCountryCode(util.substring(current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode,1,2),conn)
                                if(queryCountry){
                                    'E3232_04' queryCountry
                                }else if(current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode){

                                    'E3232_04' util.substring(current_Body?.Event?.Location?.CSStandardCity?.CSCountryCode,1,2)
                                }
                            }
                            'E5479_05' ''
                        }

                        OceanLeg lastOceanleg = null
                        if(current_Body?.Route?.OceanLeg){
                            lastOceanleg = current_Body?.Route?.OceanLeg[-1]
                        }
                        /////////////////////////////////Group6 map Route info/////////////////////////////////////////////////////////////////////
                        //TDT need to enhace which is trigger by scr
                        def querySCACid = ctUtil.getCodeConversionbyConvertTypeSCAC('CS1CarrierID',SCAC,conn)
                        current_Body?.Route?.OceanLeg?.each {current_OceanLeg->
                            'Group6_TDT' {
                                if(shipDir=='I'){
                                    'TDT' {
                                        'E8051_01' '20'

                                        if(current_OceanLeg?.SVVD?.Discharge?.ExternalVoyage){
                                            'E8028_02' util.substring(current_OceanLeg?.SVVD?.Discharge?.ExternalVoyage,1,17)
                                        }else if(current_OceanLeg?.SVVD?.Discharge?.Voyage || current_OceanLeg?.SVVD?.Discharge?.Direction){
                                            'E8028_02' util.substring(ctUtil.emptyToString(current_OceanLeg?.SVVD?.Discharge?.Voyage)+ctUtil.emptyToString(current_OceanLeg?.SVVD?.Discharge?.Direction),1,17)
                                        }

                                        'C220_03' {
                                            'E8067_01' '1'
                                            'E8066_02' ''
                                        }

                                        if(!(SCAC in ['OOLU','COSU'])){
                                            'C040_05' {
                                                // Tibco logic need query db : select Ext_CDE from B2B_CDE_CONVERSION where convert_type_id = 'CS1CarrierID'and SCAC_CDE = ?
                                                if(querySCACid){
                                                    'E3127_01' querySCACid
                                                }

                                                'E1131_02' '172'
                                                'E3055_03' '182'
                                                'E3128_04' ''
                                            }
                                        }

                                        'C222_08' {
                                            if(current_OceanLeg?.SVVD?.Discharge?.LloydsNumber){
                                                'E8213_01' util.substring(current_OceanLeg?.SVVD?.Discharge?.LloydsNumber,1,20)
                                            }else if(current_OceanLeg?.SVVD?.Discharge?.CallSign){
                                                'E8213_01' util.substring(current_OceanLeg?.SVVD?.Discharge?.CallSign,1,10)
                                            }
                                            if(current_OceanLeg?.SVVD?.Discharge?.LloydsNumber || current_OceanLeg?.SVVD?.Loading?.LloydsNumber){

                                                'E1131_02' 'ZZ'
                                            }else if(current_OceanLeg?.SVVD?.Discharge?.CallSign || current_OceanLeg?.SVVD?.Loading?.CallSign){
                                                'E1131_02' '103'
                                            }else {
                                                'E1131_02' 'ZZZ'
                                            }

                                            'E3055_03' ''
                                            if(current_OceanLeg?.SVVD?.Discharge?.VesselName){
                                                'E8212_04' util.substring(current_OceanLeg?.SVVD?.Discharge?.VesselName,1,30)
                                            }

                                            'E8453_05' ''
                                        }
                                        'E8281_09' ''
                                    }
                                }else if(shipDir=='O') {
                                    'TDT' {
                                        'E8051_01' '20'
                                        if(current_OceanLeg?.SVVD?.Loading?.ExternalVoyage){
                                            'E8028_02' util.substring(current_OceanLeg?.SVVD?.Loading?.ExternalVoyage,1,17)
                                        } else if (current_OceanLeg?.SVVD?.Loading?.Voyage || current_OceanLeg?.SVVD?.Loading?.Direction) {
                                            'E8028_02' util.substring(ctUtil.emptyToString(current_OceanLeg?.SVVD?.Loading?.Voyage) + ctUtil.emptyToString(current_OceanLeg?.SVVD?.Loading?.Direction), 1, 17)}
                                            'C220_03' {
                                                'E8067_01' '1'
                                                'E8066_02' ''
                                            }
                                        if(!(SCAC in ['OOLU','COSU'])){
                                            'C040_05' {
                                                // Tibco logic need query db : select Ext_CDE from B2B_CDE_CONVERSION where convert_type_id = 'CS1CarrierID'and SCAC_CDE = ?
                                                if(querySCACid){
                                                    'E3127_01' querySCACid
                                                }
                                                'E1131_02' '172'
                                                'E3055_03' '182'
                                                'E3128_04' ''
                                            }
                                        }
                                            'C222_08' {
                                                if(current_OceanLeg?.SVVD?.Loading?.LloydsNumber){
                                                    'E8213_01' util.substring(current_OceanLeg?.SVVD?.Loading?.LloydsNumber,1,20)
                                                }else if(current_OceanLeg?.SVVD?.Loading?.CallSign){
                                                    'E8213_01' util.substring(current_OceanLeg?.SVVD?.Loading?.CallSign,1,10)
                                                }

                                                if(current_OceanLeg?.SVVD?.Discharge?.LloydsNumber || current_OceanLeg?.SVVD?.Loading?.LloydsNumber){
                                                    'E1131_02' 'ZZ'
                                                }else if(current_OceanLeg?.SVVD?.Discharge?.CallSign || current_OceanLeg?.SVVD?.Loading?.CallSign){
                                                    'E1131_02' '103'
                                                }else {
                                                    'E1131_02' 'ZZZ'
                                                }
                                                'E3055_03' ''
                                                if(current_OceanLeg?.SVVD?.Loading?.VesselName){
                                                    'E8212_04' util.substring(current_OceanLeg?.SVVD?.Loading?.VesselName,1,30)
                                                }
                                                'E8453_05' ''
                                            }
                                            'E8281_09' ''
                                        }
                                    }


                                //map LOC from FND
                                'LOC' {
                                    if(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode || current_Body?.Route?.FND?.CityDetails?.City || current_Body?.Route?.FND?.CityDetails?.Country){
                                        'E3227_01' '7'
                                        'C517_02' {
                                            if(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode){
                                                'E3225_01' util.substring(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode,1,5)
                                            }
                                            'E1131_02' '229'
                                            'E3055_03' '6'
                                            if(current_Body?.Route?.FND?.CityDetails?.City){
                                                'E3224_04' util.substring(current_Body?.Route?.FND?.CityDetails?.City,1,60)
                                            }
                                        }
                                        'C519_03' {
                                            if(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode) {
                                                'E3223_01' util.substring(current_Body?.Route?.FND?.CityDetails?.LocationCode?.UNLocationCode, 1, 5)
                                            }
                                            'E1131_02' '145'
                                            'E3055_03' '6'
                                            if(current_Body?.Route?.FND?.CityDetails?.City){
                                                'E3222_04' util.substring(current_Body?.Route?.FND?.CityDetails?.City,1,60)
                                            }
                                        }
                                        'C553_04' {
                                            if(current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode){
                                                'E3233_01' util.substring(current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode,1,2)
                                            }

                                            'E1131_02' '162'
                                            'E3055_03' '6'
                                            if(current_Body?.Route?.FND?.CityDetails?.Country)
                                            {
                                                'E3232_04' util.substring(current_Body?.Route?.FND?.CityDetails?.Country,1,35)
                                            }
                                        }
                                        'E5479_05' ''
                                    }
                                }
                                //map LOC from POL
                                'LOC'{
                                    if(current_OceanLeg?.POL?.Port?.LocationCode?.UNLocationCode || current_OceanLeg?.POL?.Port?.City || current_OceanLeg?.POL?.Port?.Country){
                                        'E3227_01' '9'
                                        'C517_02' {
                                            if(current_OceanLeg?.POL?.Port?.LocationCode?.UNLocationCode){
                                                'E3225_01' util.substring(current_OceanLeg?.POL?.Port?.LocationCode?.UNLocationCode,1,5)
                                            }
                                            'E1131_02' '139'
                                            'E3055_03' '6'
                                            if(current_OceanLeg?.POL?.Port?.PortName){
                                                'E3224_04' util.substring(current_OceanLeg?.POL?.Port?.PortName,1,35)
                                            }
                                        }
                                        'C519_03' {
                                            if(current_OceanLeg?.POL?.Port?.LocationCode?.UNLocationCode){
                                                'E3223_01' util.substring(current_OceanLeg?.POL?.Port?.LocationCode?.UNLocationCode,1,5)
                                            }
                                            'E1131_02' '145'
                                            'E3055_03' '6'
                                            if(current_OceanLeg?.POL?.Port?.City){
                                                'E3222_04' util.substring(current_OceanLeg?.POL?.Port?.City,1,60)
                                            }
                                        }
                                        'C553_04' {
                                            if(current_OceanLeg?.POL?.Port?.CSCountryCode){
                                                'E3233_01' util.substring(current_OceanLeg?.POL?.Port?.CSCountryCode,1,2)
                                            }
                                            'E1131_02' '162'
                                            'E3055_03' '6'
                                            if(current_OceanLeg?.POL?.Port?.Country){
                                                'E3232_04' util.substring(current_OceanLeg?.POL?.Port?.Country,1,35)
                                            }

                                        }

                                    }else if(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode || current_Body?.Route?.FirstPOL?.Port?.City || current_Body?.Route?.FirstPOL?.Port?.Country){
                                        'E3227_01' '9'
                                        'C517_02' {
                                            if(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode){
                                                'E3225_01' util.substring(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode,1,5)
                                            }
                                            'E1131_02' '139'
                                            'E3055_03' '6'
                                            if(current_Body?.Route?.FirstPOL?.Port?.PortName){
                                                'E3224_04' util.substring(current_Body?.Route?.FirstPOL?.Port?.PortName,1,35)
                                            }
                                        }
                                        'C519_03' {
                                            if(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode){
                                                'E3223_01' util.substring(current_Body?.Route?.FirstPOL?.Port?.LocationCode?.UNLocationCode,1,5)
                                            }
                                            'E1131_02' '145'
                                            'E3055_03' '6'
                                            if(current_Body?.Route?.FirstPOL?.Port?.City){
                                                'E3222_04' util.substring(current_Body?.Route?.FirstPOL?.Port?.City,1,60)
                                            }
                                        }
                                        'C553_04' {
                                            if(current_Body?.Route?.FirstPOL?.Port?.CSCountryCode){
                                                'E3233_01' util.substring(current_Body?.Route?.FirstPOL?.Port?.CSCountryCode,1,2)
                                            }
                                            'E1131_02' '162'
                                            'E3055_03' '6'
                                            if(current_Body?.Route?.FirstPOL?.Port?.Country){
                                                'E3232_04' util.substring(current_Body?.Route?.FirstPOL?.Port?.Country,1,35)
                                            }

                                        }

                                    }

                                }
                                //map from pod
                                'LOC' {
                                    if (current_OceanLeg?.POD?.Port?.LocationCode?.UNLocationCode || current_OceanLeg?.POD?.Port?.City || current_OceanLeg?.POD?.Port?.Country) {
                                        'E3227_01' '11'
                                        'C517_02' {
                                            if(current_OceanLeg?.POD?.Port?.LocationCode?.UNLocationCode){
                                                'E3225_01' util.substring(current_OceanLeg?.POD?.Port?.LocationCode?.UNLocationCode,1,5)
                                            }
                                            'E1131_02' '139'
                                            'E3055_03' '6'
                                            if(current_OceanLeg?.POD?.Port?.PortName){
                                                'E3224_04' util.substring(current_OceanLeg?.POD?.Port?.PortName,1,35)
                                            }

                                        }
                                        'C519_03' {
                                            if(current_OceanLeg?.POD?.Port?.LocationCode?.UNLocationCode){
                                                'E3223_01' util.substring(current_OceanLeg?.POD?.Port?.LocationCode?.UNLocationCode,1,5)
                                            }
                                            'E1131_02' '145'
                                            'E3055_03' '6'
                                            if(current_OceanLeg?.POD?.Port?.City){
                                                'E3222_04' util.substring(current_OceanLeg?.POD?.Port?.City,1,60)
                                            }
                                        }
                                        'C553_04' {
                                            if(current_OceanLeg?.POD?.Port?.CSCountryCode){
                                                'E3233_01'  util.substring(current_OceanLeg?.POD?.Port?.CSCountryCode,1,2)
                                            }

                                            'E1131_02' '162'
                                            'E3055_03' '6'
                                            if(current_OceanLeg?.POD?.Port?.Country){
                                                'E3232_04' util.substring(current_OceanLeg?.POD?.Port?.Country,1,35)
                                            }

                                        }
                                        'E5479_05' ''
                                    }
                                }
                                //map from por
                                'LOC'{
                                    if(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode || current_Body?.Route?.POR?.CityDetails?.City || current_Body?.Route?.POR?.CityDetails?.Country){
                                        'E3227_01' '88'
                                        'C517_02' {
                                            if(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode){
                                                'E3225_01' util.substring(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode,1,5)
                                            }
                                            'E1131_02' '229'
                                            'E3055_03' '6'
                                            if(current_Body?.Route?.POR?.CityDetails?.City){
                                                'E3224_04' util.substring(current_Body?.Route?.POR?.CityDetails?.City,1,60)
                                            }
                                        }
                                        'C519_03' {
                                            if(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode){
                                                'E3223_01' util.substring(current_Body?.Route?.POR?.CityDetails?.LocationCode?.UNLocationCode,1,5)
                                            }
                                            'E1131_02' '145'
                                            'E3055_03' '6'
                                            if(current_Body?.Route?.POR?.CityDetails?.City){
                                                'E3222_04' util.substring(current_Body?.Route?.POR?.CityDetails?.City,1,60)
                                            }
                                        }
                                        'C553_04' {
                                            if(current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode){
                                                'E3233_01' util.substring(current_Body?.Route?.POR?.CSStandardCity?.CSCountryCode,1,2)
                                            }

                                            'E1131_02' '162'
                                            'E3055_03' '6'
                                            if(current_Body?.Route?.POR?.CityDetails?.Country){
                                                'E3232_04' util.substring(current_Body?.Route?.POR?.CityDetails?.Country,1,35)
                                            }
                                        }
                                        'E5479_05' ''
                                    }

                                }
                                //#1 map DTM from Oceanleg/POD/ArrivalDT
                                def flagPODAct = false
                                if(current_OceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator =='A'}?.LocDT){
                                    flagPODAct = true
                                    'DTM' {
                                        'C507_01' {
                                            'E2005_01' '178'
                                            'E2380_02' util.convertXmlDateTime(current_OceanLeg?.POD?.ArrivalDT?.find{it?.attr_Indicator =='A'}?.LocDT,"yyyyMMddHHmm")
                                            'E2379_03' '203'
                                        }
                                    }
                                }
                                //#2 map DTM from Oceanleg/POL/DepatchDT
                                if(current_OceanLeg?.POL?.DepartureDT?.find {it?.attr_Indicator=='A'}?.LocDT){
                                    'DTM' {
                                        'C507_01' {
                                            'E2005_01' '186'
                                            'E2380_02' util.convertXmlDateTime(current_OceanLeg?.POL?.DepartureDT?.find {it?.attr_Indicator=='A'}?.LocDT,"yyyyMMddHHmm")
                                            'E2379_03' '203'
                                        }
                                    }
                                }else if(current_OceanLeg?.POL?.DepartureDT?.find {it?.attr_Indicator=='E'}?.LocDT){
                                    'DTM' {
                                        'C507_01' {
                                            'E2005_01' '133'
                                            'E2380_02' util.convertXmlDateTime(current_OceanLeg?.POL?.DepartureDT?.find {it?.attr_Indicator=='E'}?.LocDT,"yyyyMMddHHmm")
                                            'E2379_03' '203'
                                        }
                                    }
                                }

                                if(!flagPODAct && lastOceanleg?.POD?.ArrivalDT?.find {it?.attr_Indicator=='E'}?.LocDT){
                                    'DTM' {
                                        'C507_01' {
                                        'E2005_01' '132'
                                        'E2380_02' util.convertXmlDateTime(lastOceanleg?.POD?.ArrivalDT?.find {it?.attr_Indicator=='E'}?.LocDT,"yyyyMMddHHmm")
                                        'E2379_03' '203'
                                    }
                                }
                                }

                            }
                        }
                        Seal lastSeal = null
                        if(current_Body?.Container?.Seal){
                            lastSeal = current_Body?.Container?.Seal[-1]
                        }

                        'Group7_EQD' {
                            'EQD' {
                                'E8053_01' 'CN'
                                'C237_02' {
                                    if(current_Body?.Container?.ContainerNumber || current_Body?.Container?.ContainerCheckDigit){
                                        'E8260_01' util.substring(ctUtil.emptyToString(current_Body?.Container?.ContainerNumber),1,10) +util.substring(ctUtil.emptyToString(current_Body?.Container?.ContainerCheckDigit),1,2)
                                    }

                                    'E1131_02' ''
                                    'E3055_03' ''
                                    'E3207_04' ''
                                }
                                'C224_03' {
                                    if(current_Body?.Container?.CarrCntrSizeType){
                                        'E8155_01' util.substring(current_Body?.Container?.CarrCntrSizeType,1,4)
                                    }

                                    'E1131_02' ''
                                    'E3055_03' ''
                                    'E8154_04' ''
                                }
                                'E8077_04' ''
                                'E8249_05' ''
                                if(current_Body?.Event?.CS1Event in ['CS010','CS210'] ){
                                    'E8169_06' '4'
                                }else {
                                    'E8169_06' '5'
                                }

                            }
                            def sealTypeMap = ['TM':'TO','OT':'AB']
                            if(lastSeal?.SealNumber && lastSeal?.SealNumber?.length()>=4){
                                'SEL' {
                                    if(lastSeal?.SealNumber){
                                        'E9308_01' util.substring(lastSeal?.SealNumber,1,10)
                                    }
                                    'C215_02' {
                                        if(sealTypeMap.containsKey(lastSeal?.SealType)){
                                            'E9303_01' sealTypeMap.get(lastSeal?.SealType)
                                        }else if(lastSeal?.SealType){
                                            'E9303_01' lastSeal?.SealType
                                        }

                                        'E1131_02' ''
                                        'E3055_03' ''
                                        'E9302_04' ''
                                    }
                                    'E4517_03' ''
                                }
                            }


                        }
                    }
                }
                'UNT' {
                    'E0074_01' '-999'
                    'E0062_02' '-999'
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
        def IFTSTA = outXml.createNode('IFTSTA')
        def bizKeyRoot = bizKeyXml.createNode('root')
        def csuploadRoot = csuploadXml.createNode('root')	//csupload root node name must be 'root', or will cause ORA error.


        //BeginWorkFlow
        TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
        def outputBodyCount = 0
        currentSystemDt = new Date()
        //def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, "X.12", conn)

        headerMsgDT = util.convertXmlDateTime(ct.Header.MsgDT.LocDT, 'yyyyMMddHHmmss')
        //long ediIsaCtrlNumber = ctrlNos[0]
        //long ediGroupCtrlNum = ctrlNos[1]
        def txnErrorKeys = []
        //duplication -- CT special logic
//		Map<String,String> duplicatedPairs = ctUtil.getDuplicatedPairs(TP_ID, conn)
//
//		List<Body> blDupBodies = ctUtil.CTBLDuplication(ct.Body)
//		List<Body> bodies = ctUtil.CTEventDuplication(blDupBodies, duplicatedPairs)

        //start body looping
        ct.Body.eachWithIndex { current_Body, current_BodyIndex ->


            def eventCS2Cde = current_Body.Event.CS1Event
            def eventExtCde = util.getConversion('INFODISBVFNL1014430', 'CT', 'O', 'EventCdeCSIntVal', eventCS2Cde, conn)

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

        //EndWorkFlow

        //End root node
        outXml.nodeCompleted(null,IFTSTA)
        bizKeyXml.nodeCompleted(null,bizKeyRoot)
        csuploadXml.nodeCompleted(null,csuploadRoot)


        ctUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
        ctUtil.promoteCSUploadToSession(appSessionId, csuploadWriter);
        ctUtil.promoteAssoInterchangeMessageIDToSession(appSessionId,ct.Header.InterchangeMessageID);
        if(ct.Body[0]?.GeneralInformation?.SCAC)ctUtil.promoteAssoCarrierSCACToSession(appSessionId,ct.Body[0]?.GeneralInformation?.SCAC);

        String result = "";
        if (txnErrorKeys.findAll{it.size == 0}.size != 0) {
            //if exists one txn without error, then return result
            result = writer?.toString();
            result = util.cleanXml(result)
        }

        writer.close();
        csuploadWriter.close()
        bizKeyWriter.close()

        return result;
    }

    public List<Map<String,String>> prepValidation(Body current_Body, def current_BodyIndex, def eventCS2Cde, def eventExtCde) {
        List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>();
        ctUtil.missingEventStatusCode(eventCS2Cde,eventExtCde,true,' - Event not subscribed by Partner.',errorKeyList)
        ctUtil.missingEventLocation(eventCS2Cde,current_Body?.Event,true,null,errorKeyList)

        def missingFNDLocationMsg =   " - Missing FND UN Location Code for "+ctUtil.emptyToString(current_Body?.Route?.FND?.CityDetails?.City)+'\\'+ctUtil.emptyToString(current_Body?.Route?.FND?.CSStandardCity?.CSStateCode)+'\\'+ctUtil.emptyToString(current_Body?.Route?.FND?.CSStandardCity?.CSCountryCode)
        ctUtil.missingFNDLocation(eventCS2Cde,current_Body?.Route?.FND,true,missingFNDLocationMsg,errorKeyList)

        def missingPODLocationMsg = "  - Missing POD UN Location Code for "+ctUtil.emptyToString(current_Body?.Route?.LastPOD?.Port?.City)+'\\'+ctUtil.emptyToString(current_Body?.Route?.LastPOD?.Port?.State)+'\\'+ctUtil.emptyToString(current_Body?.Route?.LastPOD?.Port?.CSCountryCode)
        current_Body?.Route?.OceanLeg?.each {current_OceanLeg ->
            ctUtil.missingPODLocation(eventCS2Cde,current_Body?.Route?.LastPOD,current_OceanLeg,true,missingPODLocationMsg,errorKeyList)
        }

        return errorKeyList;
    }

    public boolean pospValidation() {

    }


}


