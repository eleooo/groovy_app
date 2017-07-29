package cs.b2b.mapping.scripts

//import cs.b2b.core.common.util.StringUtil
//import cs.b2b.core.mapping.bean.br.*
//import cs.b2b.core.mapping.bean.*
//import cs.b2b.core.mapping.util.XmlBeanParser
//import groovy.xml.MarkupBuilder
//import org.apache.commons.lang3.StringUtils
//
//import java.sql.Connection
//import java.text.DecimalFormat
//
import cs.b2b.core.mapping.bean.ContactFax
import cs.b2b.core.mapping.bean.ContactPhone
import cs.b2b.core.mapping.bean.br.*
import cs.b2b.core.mapping.util.XmlBeanParser
import groovy.xml.MarkupBuilder
import org.apache.commons.lang3.StringUtils

import javax.xml.bind.DatatypeConverter
import java.sql.Connection
import java.text.DecimalFormat
import java.text.SimpleDateFormat


/**
 *
 * @author CHEUNBE2
 *
 */
public class CAR_CS2BRXML_IFTMBF_CMACGM {

    cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil()

    def appSessionId = null
    def sourceFileName = null
    def TP_ID = null
    def MSG_TYPE_ID = null
    def DIR_ID = null
    def MSG_REQ_ID = null
    Connection conn = null

    java.util.Date currentDate = new java.util.Date()
    def yyyyMMdd = "yyyyMMdd"
    def HHmm = 'HHmm'

    def currentSystemDt = null
    DecimalFormat decimalFormatter = new DecimalFormat("#.####")
    DecimalFormat decimalFormatterNoDigital = new DecimalFormat("#")
    def dataSource = null

//	Map bookingStatusMap = ['Confirmed':'A','Pending':'B','Wait Listed':'B','Cancelled':'D','No Show':'D','Declined':'R','Rejected':'R']

//	Map<String,String> referenceTypeMap = ['BL':'BM','INV':'IN', 'CGO':'CG', 'CR':'CR', 'CTR':'CT', 'FM':'FM', 'FR':'FN', 'PO':'PO', 'PR':'Q1', 'RP':'RP', 'SC':'E8', 'SID':'SI', 'SO':'SO', 'SR':'SR', 'TARIF':'TS']

    void generateBody(Request currentBody, MarkupBuilder outXml) {
        outXml.'Group_UNH' {
            'UNH' {
                'E0062_01' '1'
                'S009_02' {
                    'E0065_01' 'IFTMBF'
                    'E0052_02' 'D'
                    'E0054_03' '99B'
                    'E0051_04' 'UN'
                    'E0057_05' '2.0'
                    'E0110_06' ''
                    'E0113_07' ''
                }
                'E0068_03' ''
                'S010_04' {
                    'E0070_01' ''
                    'E0073_02' ''
                }
            }
            'BGM' {
                'C002_01' {
                    'E1001_01' '335'
                    'E1131_02' ''
                    'E3055_03' ''
                    Map dataSourceMap = [
                            'CS1desktop'  : 'CS-Application',
                            'CS1excel'    : 'CS-Application',
                            'CS1online'   : 'CS-Application',
                            'CS2online'   : 'CS-Application',
                            'blamendment' : 'CS-Application',
                            'draftblamd'  : 'CS-Application',
                            'vdc_customer': 'CS-Application',
                            'B2B'         : 'CS-Integration',
                            'CS1edi'      : 'CS-Integration',
                            'CS1sedi'     : 'CS-Integration',
                            'vec_edi'     : 'CS-Integration',
                            'simple'      : 'CS-VDC',
                            'simpleauto'  : 'CS-VDC',
                            'vdc'         : 'CS-VDC',
                            'vec_online'  : 'CS-VDC'
                    ]
                    'E1000_04' dataSourceMap.find { it.key == dataSource }?.value
                }
                'C106_02' {
                    'E1004_01' currentBody.GeneralInformation?.CSBookingRefNumber
                    'E1056_02' ''
                    'E1060_03' ''
                }
                switch (currentBody.GeneralInformation.ActionType.substring(0, 3).toUpperCase()) {
                    case ('NEW'):
                        'E1225_03' '9'
                        break
                    case ('UPD'):
                        'E1225_03' '4'
                        break
                    case ('CAN'):
                        'E1225_03' '1'
                        break
                }
                if (currentBody.GeneralInformation.ActionType.substring(0, 3).toUpperCase() == 'NEW') {
                    'E4343_04' 'AC'
                }
            }
            def bookingParty = currentBody.Party.find {
                it.PartyType == 'BPT' && it?.Contact?.FirstName != "" && it?.Contact?.LastName != ""
            }
            if (bookingParty) {
                'CTA' {
                    'E3139_01' 'IC'
                    'C056_02' {
                        'E3413_01' ''
                        if ("$currentBody.Remarks".find { it.contains('CTC:"') }) {
                            'E3412_02' StringUtils.substringBefore(StringUtils.substringAfter(currentBody.Remarks.Remarks.find {
                                it.startsWith('CTC:"')
                            }, 'CTC:"'), '"')
                        } else {
                            if (bookingParty?.Contact?.LastName) {
                                'E3412_02' bookingParty?.Contact?.FirstName + " " + bookingParty?.Contact?.LastName
                            } else {
                                'E3412_02' bookingParty?.Contact?.FirstName
                            }
                        }
                    }
                }
                def phoneNo
                def faxNo
                def emailAddress
                if ("$currentBody.Remarks".find { it.contains('CTC:"') }) {
                    phoneNo = StringUtils.substringBefore(StringUtils.substringAfter("$currentBody.Remarks".find { it.contains('TEL:"') }), '"')
                    faxNo = StringUtils.substringBefore(StringUtils.substringAfter("$currentBody.Remarks".find { it.contains('FAX:"') }), '"')
                    emailAddress = StringUtils.substringBefore(StringUtils.substringAfter("$currentBody.Remarks".find { it.contains('Email:""') }), '"')
                    if (phoneNo) {
                        'COM' {
                            'C076_01' {
                                'E3148_01' phoneNo
                                'E3155_02' 'TE'
                            }
                        }
                    }
                    if (faxNo) {
                        'COM' {
                            'C076_01' {
                                'E3148_01' faxNo
                                'E3155_02' 'FX'
                            }
                        }
                    }
                    if (emailAddress) {
                        'COM' {
                            'C076_01' {
                                'E3148_01' emailAddress
                                'E3155_02' 'EM'
                            }
                        }
                    }
                } else {
                    phoneNo = constructContactNumber(bookingParty?.Contact?.ContactPhone)
                    faxNo = constructContactNumber(bookingParty?.Contact?.ContactFax)
                    emailAddress = bookingParty?.Contact?.ContactEmailAddress
                    if (faxNo) {
                        'COM' {
                            'C076_01' {
                                'E3148_01' faxNo
                                'E3155_02' 'FX'
                            }
                        }
                    }
                    if (phoneNo) {
                        'COM' {
                            'C076_01' {
                                'E3148_01' phoneNo
                                'E3155_02' 'TE'
                            }
                        }
                    }
                    if (emailAddress) {
                        'COM' {
                            'C076_01' {
                                'E3148_01' emailAddress
                                'E3155_02' 'EM'
                            }
                        }
                    }
                }
            }
            if (currentBody.GeneralInformation?.Requested?.RequestedDT?.GMT || currentBody.GeneralInformation?.Requested?.RequestedDT?.LocDT) {
                'DTM' {
                    'C507_01' {
                        'E2005_01' '137'
                        if (currentBody.GeneralInformation?.Requested?.RequestedDT?.LocDT) {
                            'E2380_02' convertXmlDateToEdiDate(currentBody.GeneralInformation.Requested.RequestedDT.LocDT, 'yyyyMMddHHmm')
                        } else {
                            'E2380_02' convertXmlDateToEdiDate(currentBody.GeneralInformation.Requested.RequestedDT.GMT, 'yyyyMMddHHmm')
                        }
                        'E2379_03' '203'
                    }
                }
            }
            currentBody?.Container?.Haulage.findAll() { it?.OutBound && it?.InBound }?.last()?.each { haulage ->
                'TSR' {
                    'C536_01' {
                        if (haulage.OutBound == 'C' && haulage.InBound == 'C') {
                            'E4065_01' '27'
                        }
                        if (haulage.OutBound == 'C' && haulage.InBound == 'M') {
                            'E4065_01' '28'
                        }
                        if (haulage.OutBound == 'M' && haulage.InBound == 'C') {
                            'E4065_01' '29'
                        }
                        if (haulage.OutBound == 'M' && haulage.InBound == 'M') {
                            'E4065_01' '30'
                        }
                        'E1131_02' ''
                        'E3055_03' ''
                    }
                    'C233_02' {
                        'E7273_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E7273_04' ''
                        'E1131_05' ''
                        'E3055_06' ''
                    }
                    'C537_03' {
                        'E4219_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                    }
                    'C703_04' {
                        'E7085_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                    }
                }
            }
            'MOA' {
                'C516_01' {
                    'E5025_01' ''
                    'E5004_02' ''
                    'E6345_03' ''
                    'E6343_04' ''
                    'E4405_05' ''
                }
            }
            'FTX' {
                'E4451_01' ''
                'E4453_02' ''
                'C107_03' {
                    'E4441_01' ''
                    'E1131_02' ''
                    'E3055_03' ''
                }
                'C108_04' {
                    'E4440_01' ''
                    'E4440_02' ''
                    'E4440_03' ''
                    'E4440_04' ''
                    'E4440_05' ''
                }
                'E3453_05' ''
                'E4447_06' ''
            }
            'CNT' {
                'C270_01' {
                    'E6069_01' ''
                    'E6066_02' ''
                    'E6411_03' ''
                }
            }
            if (currentBody.Cargo.CargoInfo.findAll { it.CargoNature == 'AW'}) {
                'GDS' {
                    'C703_01' {
                        'E7085_01' '5'
                    }
                    'E1131_02' ''
                    'E3055_03' ''
                }
            }
            if (currentBody.Cargo.CargoInfo.findAll { it.CargoNature == 'RF'}) {
                'GDS' {
                    'C703_01' {
                        'E7085_01' '14'
                    }
                    'E1131_02' ''
                    'E3055_03' ''
                }
            }
            if (currentBody.Cargo.CargoInfo.findAll { it.CargoNature == 'DG' }) {
                'GDS' {
                    'C703_01' {
                        'E7085_01' '11'
                    }
                    'E1131_02' ''
                    'E3055_03' ''
                }
            }
            if (currentBody?.Container?.Route?.POR?.CityDetails?.City){
                //por
                def por = currentBody.Container.Route.POR.first()
                'Group1_LOC' {
                    'LOC' {
                        'E3227_01' '88'
                        'C517_02' {
                            def unLocationCode
                            if (por?.CityDetails?.LocationCode?.UNLocationCode){
                                unLocationCode = por.CityDetails.LocationCode.UNLocationCode
                            } else if (por?.CityDetails?.LocationCode?.SchedKDCode){
                                unLocationCode = por.CityDetails.LocationCode.SchedKDCode
                            } else if (currentBody?.Container?.Route?.OceanLeg?.first()?.POL?.Facility?.FacilityCode){
                                unLocationCode = currentBody?.Container?.Route?.OceanLeg?.first()?.POL?.Facility?.FacilityCode
                            }

                            if (unLocationCode) {
                                'E3225_01' convertUnLocationCode(unLocationCode)
                            }
                            'E1131_02' '181'
                            'E3055_03' '6'
                            'E3224_04' por.CityDetails.City
                        }
                        'C519_03' {
                            if (por?.CSStandardCity?.CSCountryCode) {
                                'E3223_01' por?.CSStandardCity?.CSCountryCode
                                'E1131_02' '162'
                                'E3055_03' '5'
                            }
                            'E3222_04' por?.CityDetails?.Country
                        }
                        'C553_04' {
                            'E3233_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                            'E3232_04' por?.CityDetails?.State
                        }
                        'E5479_05' ''
                    }
                    if (currentBody?.Container?.Route?.IntendedDT?.find {it.IntendedRangeIndicator == 'S'}?.From?.LocDT ||
                            currentBody?.Container?.Route?.LoadingPortLatestDepDT?.LocDT ||
                            currentBody?.Container?.Route?.OceanLeg?.first()?.ETD?.LocDT) {
                        'DTM' {
                            'C507_01' {
                                    'E2005_01' '196'
                                if (currentBody?.Container?.Route?.IntendedDT?.find { it.IntendedRangeIndicator == 'S' }?.From?.LocDT) {
                                    'E2380_02' convertXmlDateToEdiDate(currentBody?.Container?.Route?.IntendedDT?.find { it.IntendedRangeIndicator == 'S' }?.From?.LocDT?.first(), 'yyyyMMddHHmm')
                                } else if (currentBody?.Container?.Route?.LoadingPortLatestDepDT?.LocDT) {
                                    'E2380_02' convertXmlDateToEdiDate(currentBody?.Container?.Route?.LoadingPortLatestDepDT?.LocDT?.first(), 'yyyyMMddHHmm')
                                } else if (currentBody?.Container?.Route?.OceanLeg?.first()?.ETD?.LocDT) {
                                    'E2380_02' convertXmlDateToEdiDate(currentBody?.Container?.Route?.OceanLeg?.first()?.ETD?.LocDT?.first(), 'yyyyMMddHHmm')
                                }
                                'E2379_03' '203'
                            }
                        }
                    }
                }
            }
            if (currentBody?.Container?.Route?.FND?.CityDetails?.City){
                //fnd
                def fnd = currentBody.Container.Route.FND.first()
                'Group1_LOC' {
                    'LOC' {
                        'E3227_01' '7'
                        'C517_02' {
                            def unLocationCode
                            if (fnd?.CityDetails?.LocationCode?.UNLocationCode){
                                unLocationCode = fnd.CityDetails.LocationCode.UNLocationCode
                            } else if (fnd?.CityDetails?.LocationCode?.SchedKDCode){
                                unLocationCode = fnd.CityDetails.LocationCode.SchedKDCode
                            } else if (currentBody?.Container?.Route?.OceanLeg?.first()?.POD?.Facility?.FacilityCode){
                                unLocationCode = currentBody?.Container?.Route?.OceanLeg?.first()?.POL?.Facility?.FacilityCode
                            }

                            if (unLocationCode) {
                                'E3225_01' convertUnLocationCode(unLocationCode)
                            }
                            'E1131_02' '181'
                            'E3055_03' '6'
                            'E3224_04' fnd.CityDetails.City
                        }
                        'C519_03' {
                            if (fnd?.CSStandardCity?.CSCountryCode) {
                                'E3223_01' fnd?.CSStandardCity?.CSCountryCode
                                'E1131_02' '162'
                                'E3055_03' '5'
                            }
                            'E3222_04' fnd?.CityDetails?.Country
                        }
                        'C553_04' {
                            'E3233_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                            'E3232_04' fnd?.CityDetails?.State
                        }
                        'E5479_05' ''
                    }
                    if (currentBody?.Container?.Route?.IntendedDT?.find {it.IntendedRangeIndicator == 'A'}?.To?.LocDT ||
                            currentBody?.Container?.Route?.DischargePortLatestArrDT?.LocDT ||
                            currentBody?.Container?.Route?.OceanLeg?.first()?.ETA?.LocDT) {
                        'DTM' {
                            'C507_01' {
                                'E2005_01' '63'
                                if (currentBody?.Container?.Route?.IntendedDT?.find {it.IntendedRangeIndicator == 'A'}?.To?.LocDT) {
                                    'E2380_02' convertXmlDateToEdiDate(currentBody?.Container?.Route?.IntendedDT?.find {it.IntendedRangeIndicator == 'A'}?.To?.LocDT?.first(), 'yyyyMMddHHmm')
                                } else if (currentBody?.Container?.Route?.DischargePortLatestArrDT?.LocDT) {
                                    'E2380_02' convertXmlDateToEdiDate(currentBody?.Container?.Route?.DischargePortLatestArrDT?.LocDT?.first(), 'yyyyMMddHHmm')
                                } else if (currentBody?.Container?.Route?.OceanLeg?.first()?.ETA?.LocDT) {
                                    'E2380_02' convertXmlDateToEdiDate(currentBody?.Container?.Route?.OceanLeg?.first()?.ETA?.LocDT?.first(), 'yyyyMMddHHmm')
                                }
                                'E2379_03' '203'
                            }
                        }
                    }
                }
            }
            def bookingOfficeRemarks = "$currentBody.Remarks".find {StringUtils.substringBefore(StringUtils.substringAfter(it,'BKG-OFFICE:"'), '"')}
            if (currentBody.GeneralInformation?.BookingOffice?.UNLocCode ||
                    currentBody.GeneralInformation?.BookingOffice?.BookingOfficeName ||
                    bookingOfficeRemarks){
                //ofc
                'Group1_LOC' {
                    'LOC' {
                        'E3227_01' '197'
                        'C517_02' {
                            def unLocationCode
                            if (currentBody.GeneralInformation?.BookingOffice?.UNLocCode){
                                unLocationCode = currentBody.GeneralInformation.BookingOffice.UNLocCode
                            } else if (bookingOfficeRemarks){
                                unLocationCode = StringUtils.substringBefore(StringUtils.substringAfter(bookingOfficeRemarks,'BKG-OFFICE:"'), '"')
                            }
                            if (unLocationCode) {
                                'E3225_01' convertUnLocationCode(unLocationCode)
                            }
                            'E1131_02' '181'
                            'E3055_03' '6'
                            'E3224_04' currentBody.GeneralInformation?.BookingOffice?.BookingOfficeName
                        }
                        'C519_03' {
                            'E3223_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                            'E3222_04' ''
                        }
                        'C553_04' {
                            'E3233_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                            'E3232_04' ''
                        }
                        'E5479_05' ''
                    }
                    'DTM' {
                        'C507_01' {
                            'E2005_01' ''
                            'E2380_02' ''
                            'E2379_03' ''
                        }
                    }
                }
            }

            'Group2_TOD' {
                'TOD' {
                    'E4055_01' ''
                    'E4215_02' ''
                    'C100_03' {
                        'E4053_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E4052_04' ''
                        'E4052_05' ''
                    }
                }
                'LOC' {
                    'E3227_01' ''
                    'C517_02' {
                        'E3225_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E3224_04' ''
                    }
                    'C519_03' {
                        'E3223_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E3222_04' ''
                    }
                    'C553_04' {
                        'E3233_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E3232_04' ''
                    }
                    'E5479_05' ''
                }
            }
            def referenceTypeMap = [
                    'SC':'CT',
                    'BL':'BM',
                    'BKG':'BN',
                    'MB':'BN',
                    'PO':'ON',
                    'SID':'SI',
                    'FR':'FF',
                    'TARIF':'AFG',
                    'MVID':'AKG',
                    'CTRP':'AGB',
                    'AR':'AGE',
                    'CGR':'ANT',
                    'EX':'EX'
            ]
            //reordering
            //add 'BL'
            List<ExternalReference> reorderedExtRefList = new LinkedList<ExternalReference>()
            currentBody?.ExternalReference?.each { currentExtRef ->
              if (currentExtRef?.CSReferenceType == 'BL'){
                  reorderedExtRefList.add(currentExtRef)
              }
            }
            //add 'PO', 'FR', 'MVID', 'CTRP', 'CGR'
            currentBody?.ExternalReference?.each { currentExtRef ->
                if (currentExtRef?.CSReferenceType in ['PO', 'FR', 'MVID', 'CTRP', 'CGR']){
                    reorderedExtRefList.add(currentExtRef)
                }
            }
            //add the rest
            currentBody?.ExternalReference?.each { currentExtRef ->
                if (!(currentExtRef?.CSReferenceType in ['BL', 'PO', 'FR', 'MVID', 'CTRP', 'CGR'])){
                    reorderedExtRefList.add(currentExtRef)
                }
            }
            reorderedExtRefList.each { currentExtRef ->
                if (currentExtRef?.ReferenceNumber){
                    'Group3_RFF' {
                        'RFF' {
                            'C506_01' {
                                'E1153_01' referenceTypeMap.find {it.key == currentExtRef?.CSReferenceType}?.value?.take(3)
                                'E1154_02' currentExtRef.ReferenceNumber.take(35)
                                'E1156_03' ''
                                'E4000_04' ''
                                'E1060_05' ''
                            }
                        }
                        'DTM' {
                            'C507_01' {
                                'E2005_01' ''
                                'E2380_02' ''
                                'E2379_03' ''
                            }
                        }
                    }
                }
            }
            'Group4_GOR' {
                'GOR' {
                    'E8323_01' ''
                    'C232_02' {
                        'E9415_01' ''
                        'E9411_02' ''
                        'E9417_03' ''
                        'E9353_04' ''
                    }
                    'C232_03' {
                        'E9415_01' ''
                        'E9411_02' ''
                        'E9417_03' ''
                        'E9353_04' ''
                    }
                    'C232_04' {
                        'E9415_01' ''
                        'E9411_02' ''
                        'E9417_03' ''
                        'E9353_04' ''
                    }
                    'C232_05' {
                        'E9415_01' ''
                        'E9411_02' ''
                        'E9417_03' ''
                        'E9353_04' ''
                    }
                }
                'FTX' {
                    'E4451_01' ''
                    'E4453_02' ''
                    'C107_03' {
                        'E4441_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                    }
                    'C108_04' {
                        'E4440_01' ''
                        'E4440_02' ''
                        'E4440_03' ''
                        'E4440_04' ''
                        'E4440_05' ''
                    }
                    'E3453_05' ''
                    'E4447_06' ''
                }
                'Group5_DOC' {
                    'DOC' {
                        'C002_01' {
                            'E1001_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                            'E1000_04' ''
                        }
                        'C503_02' {
                            'E1004_01' ''
                            'E1373_02' ''
                            'E1366_03' ''
                            'E3453_04' ''
                            'E1056_05' ''
                            'E1060_06' ''
                        }
                        'E3153_03' ''
                        'E1220_04' ''
                        'E1218_05' ''
                    }
                    'DTM' {
                        'C507_01' {
                            'E2005_01' ''
                            'E2380_02' ''
                            'E2379_03' ''
                        }
                    }
                }
            }
            'Group6_TCC' {
                'TCC' {
                    'C200_01' {
                        'E8023_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E8022_04' ''
                        'E4237_05' ''
                        'E7140_06' ''
                    }
                    'C203_02' {
                        'E5243_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E5242_04' ''
                        'E5275_05' ''
                        'E1131_06' ''
                        'E3055_07' ''
                        'E5275_08' ''
                        'E1131_09' ''
                        'E3055_10' ''
                    }
                    'C528_03' {
                        'E7357_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                    }
                    'C554_04' {
                        'E5243_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                    }
                }
                'LOC' {
                    'E3227_01' ''
                    'C517_02' {
                        'E3225_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E3224_04' ''
                    }
                    'C519_03' {
                        'E3223_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E3222_04' ''
                    }
                    'C553_04' {
                        'E3233_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E3232_04' ''
                    }
                    'E5479_05' ''
                }
                'FTX' {
                    'E4451_01' ''
                    'E4453_02' ''
                    'C107_03' {
                        'E4441_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                    }
                    'C108_04' {
                        'E4440_01' ''
                        'E4440_02' ''
                        'E4440_03' ''
                        'E4440_04' ''
                        'E4440_05' ''
                    }
                    'E3453_05' ''
                    'E4447_06' ''
                }
                'CUX' {
                    'C504_01' {
                        'E6347_01' ''
                        'E6345_02' ''
                        'E6343_03' ''
                        'E6348_04' ''
                    }
                    'C504_02' {
                        'E6347_01' ''
                        'E6345_02' ''
                        'E6343_03' ''
                        'E6348_04' ''
                    }
                    'E5402_03' ''
                    'E6341_04' ''
                }
                'PRI' {
                    'C509_01' {
                        'E5125_01' ''
                        'E5118_02' ''
                        'E5375_03' ''
                        'E5387_04' ''
                        'E5284_05' ''
                        'E6411_06' ''
                    }
                    'E5213_02' ''
                }
                'EQN' {
                    'C523_01' {
                        'E6350_01' ''
                        'E6353_02' ''
                    }
                }
                'PCD' {
                    'C501_01' {
                        'E5245_01' ''
                        'E5482_02' ''
                        'E5249_03' ''
                        'E1131_04' ''
                        'E3055_05' ''
                    }
                }
                'MOA' {
                    'C516_01' {
                        'E5025_01' ''
                        'E5004_02' ''
                        'E6345_03' ''
                        'E6343_04' ''
                        'E4405_05' ''
                    }
                }
                'QTY' {
                    'C186_01' {
                        'E6063_01' ''
                        'E6060_02' ''
                        'E6411_03' ''
                    }
                }
            }
            def route = currentBody?.Container?.find {it.Route}?.Route
            if (route){
                'Group7_TDT' {
                    'TDT' {
                        'E8051_01' '20'
                        if (route?.OceanLeg?.first()?.SVVD?.ExternalVoyageNumber) {
                            'E8028_02' route?.OceanLeg?.first()?.SVVD?.ExternalVoyageNumber
                        } else if (route?.OceanLeg?.first()?.SVVD?.Voyage){
                            'E8028_02' StringUtils.leftPad(StringUtils.substring(route?.OceanLeg?.first()?.SVVD?.Voyage,0,3),3,'0') + (route?.OceanLeg?.first()?.SVVD?.Direction?:'')
                        }
                        'C220_03' {
                            'E8067_01' '1'
                            'E8066_02' ''
                        }
                        'C228_04' {
                            'E8179_01' '8'
                            'E8178_02' 'Container Ship'
                        }
                        'C040_05' {
                            'E3127_01' currentBody.GeneralInformation.SCAC
                            'E1131_02' '172'
                            'E3055_03' '182'
                            'E3128_04' ''
                        }
                        'E8101_06' ''
                        'C401_07' {
                            'E8457_01' ''
                            'E8459_02' ''
                            'E7130_03' ''
                        }
                        'C222_08' {
                            'E8213_01' route?.OceanLeg?.first()?.SVVD?.LloydsNumber
                            'E1131_02' ''
                            if (route?.OceanLeg?.first()?.SVVD?.LloydsNumber) {
                                'E3055_03' '11'
                            }
                            'E8212_04' route?.OceanLeg?.first()?.SVVD?.VesselName?.take(30)
                            'E8453_05' ''
                        }
                        'E8281_09' ''
                    }
                    'DTM' {
                        'C507_01' {
                            'E2005_01' ''
                            'E2380_02' ''
                            'E2379_03' ''
                        }
                    }
                    'TSR' {
                        'C536_01' {
                            'E4065_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                        }
                        'C233_02' {
                            'E7273_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                            'E7273_04' ''
                            'E1131_05' ''
                            'E3055_06' ''
                        }
                        'C537_03' {
                            'E4219_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                        }
                        'C703_04' {
                            'E7085_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                        }
                    }
                    //pol
                    if (route?.FirstPOL?.Port?.PortName ||
                            route?.FirstPOL?.Port?.LocationCode?.UNLocationCode ||
                            route?.FirstPOL?.Port?.CSCountryCode ||
                            route?.FirstPOL?.Port?.Country ||
                            route?.FirstPOL?.Port?.State ||
                            route?.OceanLeg?.first()?.POL?.Port?.City ||
                            route?.OceanLeg?.first()?.POL?.Port?.PortName) {
                        'Group8_LOC' {
                            'LOC' {
                                'E3227_01' '9'
                                'C517_02' {
                                    'E3225_01' convertUnLocationCode(route?.FirstPOL?.Port?.LocationCode?.UNLocationCode)?:convertUnLocationCode(route?.OceanLeg?.first()?.POL?.Port?.LocationCode?.UNLocationCode)
                                    'E1131_02' '139'
                                    'E3055_03' '6'
                                    'E3224_04' route?.FirstPOL?.Port?.PortName?:route?.OceanLeg?.first()?.POL?.Port?.City
                                }
                                'C519_03' {
                                    if (route?.FirstPOL?.Port?.CSCountryCode) {
                                        'E3223_01' route?.FirstPOL?.Port?.CSCountryCode
                                    } else if (route?.FirstPOL?.Port?.LocationCode?.UNLocationCode){
                                        'E3223_01' util.getCS2MasterCityByUNLocCityType(route?.FirstPOL?.Port?.LocationCode?.UNLocationCode, 'S', conn )
                                    } else if (route?.OceanLeg?.first()?.POL?.Port?.CSCountryCode){
                                        'E3223_01' route?.OceanLeg?.first()?.POL?.Port?.CSCountryCode
                                    }
                                    'E1131_02' '162'
                                    'E3055_03' '5'
                                    if (route?.FirstPOL?.Port?.Country) {
                                        'E3222_04' route?.FirstPOL?.Port?.Country
                                    } else if (route?.OceanLeg?.first()?.POL?.Port?.Country) {
                                        'E3222_04' route?.OceanLeg?.first()?.POL?.Port?.Country
                                    }
                                }
                                'C553_04' {
                                    'E3233_01' ''
                                    'E1131_02' ''
                                    'E3055_03' ''
                                    if (route?.FirstPOL?.Port?.State) {
                                        'E3232_04' route?.FirstPOL?.Port?.State
                                    } else if (route?.OceanLeg?.first()?.POL?.Port?.State){
                                        'E3232_04' route?.OceanLeg?.first()?.POL?.Port?.State
                                    }
                                }
                                'E5479_05' ''
                            }
                            if (route?.LoadingPortLatestDepDT?.LocDT || route?.OceanLeg?.first()?.ETD?.LocDT){
                                'DTM' {
                                    'C507_01' {
                                        'E2005_01' '133'
                                        if (route?.LoadingPortLatestDepDT?.LocDT) {
                                            'E2380_02' convertXmlDateToEdiDate(route?.LoadingPortLatestDepDT?.LocDT, 'yyyyMMddHHmm')
                                            'E2379_03' '203'
                                        } else if (route?.OceanLeg?.first()?.ETD?.LocDT){
                                            convertXmlDateToEdiDate(route?.OceanLeg?.first()?.ETD?.LocDT, 'yyyyMMdd')
                                            'E2379_03' '102'
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //pod
                    if (route?.LastPOD?.Port?.PortName ||
                            route?.LastPOD?.Port?.LocationCode?.UNLocationCode ||
                            route?.LastPOD?.Port?.CSCountryCode ||
                            route?.LastPOD?.Port?.Country ||
                            route?.LastPOD?.Port?.State ||
                            route?.OceanLeg?.first()?.POD?.Port?.City ||
                            route?.OceanLeg?.first()?.POD?.Port?.PortName){
                        'Group8_LOC' {
                            'LOC' {
                                'E3227_01' '11'
                                'C517_02' {
                                    'E3225_01' convertUnLocationCode(route?.LastPOD?.Port?.LocationCode?.UNLocationCode)?:convertUnLocationCode(route?.OceanLeg?.first()?.POD?.Port?.LocationCode?.UNLocationCode)
                                    'E1131_02' '139'
                                    'E3055_03' '6'
                                    'E3224_04' route?.LastPOD?.Port?.PortName?:route?.OceanLeg?.first()?.POD?.Port?.City
                                }
                                'C519_03' {
                                    if (route?.LastPOD?.Port?.CSCountryCode) {
                                        'E3223_01' route?.LastPOD?.Port?.CSCountryCode
                                    } else if (route?.LastPOD?.Port?.LocationCode?.UNLocationCode){
                                        'E3223_01' util.getCS2MasterCityByUNLocCityType(route?.LastPOD?.Port?.LocationCode?.UNLocationCode, 'S', conn )
                                    } else if (route?.OceanLeg?.first()?.POD?.Port?.CSCountryCode){
                                        'E3223_01' route?.OceanLeg?.first()?.POD?.Port?.CSCountryCode
                                    }
                                    'E1131_02' '162'
                                    'E3055_03' '5'
                                    if (route?.LastPOD?.Port?.Country) {
                                        'E3222_04' route?.LastPOD?.Port?.Country
                                    } else if (route?.OceanLeg?.first()?.POD?.Port?.Country){
                                        'E3222_04' route?.OceanLeg?.first()?.POD?.Port?.Country
                                    }
                                }
                                'C553_04' {
                                    'E3233_01' ''
                                    'E1131_02' ''
                                    'E3055_03' ''
                                    if (route?.LastPOD?.Port?.State) {
                                        'E3232_04' route?.LastPOD?.Port?.State
                                    } else if (route?.OceanLeg?.first()?.POD?.Port?.State){
                                        'E3232_04' route?.OceanLeg?.first()?.POD?.Port?.State
                                    }
                                }
                                'E5479_05' ''
                            }
                            if (route?.DischargePortLatestArrDT?.LocDT || route?.OceanLeg?.first()?.ETA?.LocDT) {
                                'DTM' {
                                    'C507_01' {
                                        'E2005_01' '132'
                                        if (route?.DischargePortLatestArrDT?.LocDT) {
                                            'E2380_02' convertXmlDateToEdiDate(route?.DischargePortLatestArrDT?.LocDT, 'yyyyMMddHHmm')
                                            'E2379_03' '203'
                                        } else if (route?.OceanLeg?.first()?.ETA?.LocDT){
                                            'E2380_02' convertXmlDateToEdiDate( route?.OceanLeg?.first()?.ETA?.LocDT, 'yyyyMMdd')
                                            'E2379_03' '102'
                                        }
                                    }
                                }
                            }
                        }
                    }
                    'Group9_RFF' {
                        'RFF' {
                            'C506_01' {
                                'E1153_01' ''
                                'E1154_02' ''
                                'E1156_03' ''
                                'E4000_04' ''
                                'E1060_05' ''
                            }
                        }
                        'DTM' {
                            'C507_01' {
                                'E2005_01' ''
                                'E2380_02' ''
                                'E2379_03' ''
                            }
                        }
                    }
                }
            }
            //default NAD+CA
            'Group10_NAD' {
                'NAD' {
                    'E3035_01' 'CA'
                    'C082_02' {
                        'E3039_01' currentBody.GeneralInformation.SCAC
                        'E1131_02' '160'
                        'E3055_03' '86'
                    }
                }
            }
            def partyTypeMap = [
                    'BPT':'ZZZ',
                    'CGN':'CN',
                    'FWD':'FW',
                    'ANP':'N2',
                    'NPT':'N1',
                    'PYR':'FP',
                    'SHP':'CZ'
            ]
            currentBody.Party.findAll {partyTypeMap[it.PartyType] != null }.each {currentParty ->
                'Group10_NAD' {
                    'NAD' {
                        'E3035_01' partyTypeMap[currentParty.PartyType]
                        'C082_02' {
                            if (currentParty?.CarrierCustomerCode) {
                                'E3039_01' currentParty?.CarrierCustomerCode.take(35)
                                'E1131_02' '160'
                                'E3055_03' '192'
                            } else if (currentParty?.CSCompanyID){
                                'E3039_01' convertCsCompanyIdtoCCC(currentParty?.CSCompanyID).take(35)
                                'E1131_02' '160'
                                'E3055_03' '192'
                            }
                        }
                        List partyNameLines = splitString(currentParty?.PartyName, 35)
                        String address = StringUtils.normalizeSpace(StringUtils.join(currentParty?.Address?.AddressLines?.AddressLine?.first()?.trim(),' ',
                                currentParty?.Address?.City?.trim(), ' ',
                                currentParty?.Address?.County?.trim(), ' ',
                                currentParty?.Address?.State?.trim(), ' ',
                                currentParty?.Address?.PostalCode?.trim(), ' ',
                                currentParty?.Address?.Country?.trim()))
                        List addressLines = splitString(address, 35)
                        partyNameLines.addAll(addressLines)
                        Iterator partyNameLinesIterator = partyNameLines.findAll {it.trim() != ''}.iterator()
                        'C058_03' {
                            if (partyNameLinesIterator.hasNext()) {
                                'E3124_01' partyNameLinesIterator.next()
                            }
                            if (partyNameLinesIterator.hasNext()) {
                                'E3124_02' partyNameLinesIterator.next()
                            }
                            if (partyNameLinesIterator.hasNext()) {
                                'E3124_03' partyNameLinesIterator.next()
                            }
                            if (partyNameLinesIterator.hasNext()) {
                                'E3124_04' partyNameLinesIterator.next()
                            }
                            if (partyNameLinesIterator.hasNext()) {
                                'E3124_05' partyNameLinesIterator.next()
                            }
                        }
                        'C080_04' {
                            'E3036_01' ''
                            'E3036_02' ''
                            'E3036_03' ''
                            'E3036_04' ''
                            'E3036_05' ''
                            'E3045_06' ''
                        }
                        'C059_05' {
                            if (partyNameLinesIterator.hasNext()) {
                                'E3042_01' partyNameLinesIterator.next()
                            }
                            if (partyNameLinesIterator.hasNext()) {
                                'E3042_02' partyNameLinesIterator.next()
                            }
                            'E3042_03' ''
                            'E3042_04' ''
                        }
                        'E3164_06' ''
                        'C819_07' {
                            'E3229_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                            'E3228_04' ''
                        }
                        'E3251_08' currentParty?.Address?.PostalCode.take(10)
                        'E3207_09' ''
                    }
                    'LOC' {
                        'E3227_01' ''
                        'C517_02' {
                            'E3225_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                            'E3224_04' ''
                        }
                        'C519_03' {
                            'E3223_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                            'E3222_04' ''
                        }
                        'C553_04' {
                            'E3233_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                            'E3232_04' ''
                        }
                        'E5479_05' ''
                    }
                    if (currentParty.PartyType =='BPT'){
                        if (currentParty?.Contact ||
                                ("$currentBody?.Remarks".find {StringUtils.substringBefore(StringUtils.substringAfter(it,'CTC:"'), '"') != ''} ) ||
                                ("$currentBody?.Remarks".find {StringUtils.substringBefore(StringUtils.substringAfter(it,'TEL:"'), '"') != ''} ) ||
                                ("$currentBody?.Remarks".find {StringUtils.substringBefore(StringUtils.substringAfter(it,'FAX:"'), '"') != ''} ) ||
                                ("$currentBody?.Remarks".find {StringUtils.substringBefore(StringUtils.substringAfter(it,'Email:"'), '"') != ''} )) {
                            'Group11_CTA' {
                                if ("$currentBody?.Remarks".find {StringUtils.substringBefore(StringUtils.substringAfter(it,'CTC:"'), '"') != ''} ) {
                                    'CTA' {
                                        'E3139_01' 'IC'
                                        'C056_02' {
                                            'E3413_01' ''
                                            'E3412_02' StringUtils.substringBefore(StringUtils.substringAfter("$currentBody?.Remarks".find {it.contains('CTC:"')},'CTC:"'), '"')
                                        }
                                    }
                                } else if (StringUtils.join(currentParty?.Contact?.FirstName, ' ', currentParty?.Contact?.LastName).trim() !='') {
                                    'CTA' {
                                        'E3139_01' 'IC'
                                        'C056_02' {
                                            'E3413_01' ''
                                            'E3412_02' StringUtils.join(currentParty?.Contact?.FirstName, ' ', currentParty?.Contact?.LastName).trim()
                                        }
                                    }
                                }
                                def contactMapped = false
                                if ("$currentBody?.Remarks".find {StringUtils.substringBefore(StringUtils.substringAfter(it,'TEL:"'), '"') != ''}){
                                    contactMapped = true
                                    'COM' {
                                        'C076_01' {
                                            'E3148_01' StringUtils.substringBefore(StringUtils.substringAfter("$currentBody?.Remarks".find {it.contains('TEL:"')},'TEL:"'), '"')
                                            'E3155_02' 'TE'
                                        }
                                    }
                                }
                                if ("$currentBody?.Remarks".find {StringUtils.substringBefore(StringUtils.substringAfter(it,'FAX:"'), '"') != ''}){
                                    contactMapped = true
                                    'COM' {
                                        'C076_01' {
                                            'E3148_01' StringUtils.substringBefore(StringUtils.substringAfter("$currentBody?.Remarks".find {it.contains('FAX:"')},'FAX:"'), '"')
                                            'E3155_02' 'FX'
                                        }
                                    }
                                }
                                if ("$currentBody?.Remarks".find {StringUtils.substringBefore(StringUtils.substringAfter(it,'Email:"'), '"') != ''}){
                                    contactMapped = true
                                    'COM' {
                                        'C076_01' {
                                            'E3148_01' StringUtils.substringBefore(StringUtils.substringAfter("$currentBody?.Remarks".find {it.contains('Email:"')},'Email:"'), '"')
                                            'E3155_02' 'EM'
                                        }
                                    }
                                }
                                if (!contactMapped){
                                    if (currentParty?.Contact?.ContactPhone) {
                                        'COM' {
                                            'C076_01' {
                                                'E3148_01' constructContactNumber(currentParty?.Contact?.ContactPhone)
                                                'E3155_02' 'TE'
                                            }
                                        }
                                    }
                                    if (currentParty?.Contact?.ContactEmailAddress) {
                                        'COM' {
                                            'C076_01' {
                                                'E3148_01' currentParty?.Contact?.ContactEmailAddress
                                                'E3155_02' 'EM'
                                            }
                                        }
                                    } else if (currentParty?.PartyType == 'FW' || !currentParty?.Contact?.ContactEmailAddress || currentBody?.Party?.find {it.PartyType =='BPT'}?.Contact?.ContactEmailAddress) {
                                        'COM' {
                                            'C076_01' {
                                                'E3148_01' currentBody?.Party?.find {it.PartyType =='BPT'}?.Contact?.ContactEmailAddress
                                                'E3155_02' 'EM'
                                            }
                                        }
                                    }
                                    if (currentParty?.Contact?.ContactFax) {
                                        'COM' {
                                            'C076_01' {
                                                'E3148_01' constructContactNumber(currentParty?.Contact?.ContactFax)
                                                'E3155_02' 'FX'
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (currentParty?.Contact){
                        'Group11_CTA' {
                            if (StringUtils.join(currentParty?.Contact?.FirstName, ' ', currentParty?.Contact?.LastName).trim() !='') {
                                'CTA' {
                                    'E3139_01' 'IC'
                                    'C056_02' {
                                        'E3413_01' ''
                                        'E3412_02' StringUtils.join(currentParty?.Contact?.FirstName, ' ', currentParty?.Contact?.LastName).trim()
                                    }
                                }
                            }
                            if (currentParty?.Contact?.ContactPhone) {
                                'COM' {
                                    'C076_01' {
                                        'E3148_01' constructContactNumber(currentParty?.Contact?.ContactPhone)
                                        'E3155_02' 'TE'
                                    }
                                }
                            }
                            if (currentParty?.Contact?.ContactEmailAddress) {
                                'COM' {
                                    'C076_01' {
                                        'E3148_01' currentParty?.Contact?.ContactEmailAddress
                                        'E3155_02' 'EM'
                                    }
                                }
                            }else if (currentParty?.PartyType == 'FW' || !currentParty?.Contact?.ContactEmailAddress || currentBody?.Party?.find {it.PartyType =='BPT'}?.Contact?.ContactEmailAddress) {
                                'COM' {
                                    'C076_01' {
                                        'E3148_01' currentBody?.Party?.find {it.PartyType =='BPT'}?.Contact?.ContactEmailAddress
                                        'E3155_02' 'EM'
                                    }
                                }
                            }
                            if (currentParty?.Contact?.ContactFax) {
                                'COM' {
                                    'C076_01' {
                                        'E3148_01' constructContactNumber(currentParty?.Contact?.ContactFax)
                                        'E3155_02' 'FX'
                                    }
                                }
                            }
                        }
                    }
                    'Group12_DOC' {
                        'DOC' {
                            'C002_01' {
                                'E1001_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                                'E1000_04' ''
                            }
                            'C503_02' {
                                'E1004_01' ''
                                'E1373_02' ''
                                'E1366_03' ''
                                'E3453_04' ''
                                'E1056_05' ''
                                'E1060_06' ''
                            }
                            'E3153_03' ''
                            'E1220_04' ''
                            'E1218_05' ''
                        }
                        'DTM' {
                            'C507_01' {
                                'E2005_01' ''
                                'E2380_02' ''
                                'E2379_03' ''
                            }
                        }
                    }
                    'Group13_RFF' {
                        'RFF' {
                            'C506_01' {
                                'E1153_01' ''
                                'E1154_02' ''
                                'E1156_03' ''
                                'E4000_04' ''
                                'E1060_05' ''
                            }
                        }
                        'DTM' {
                            'C507_01' {
                                'E2005_01' ''
                                'E2380_02' ''
                                'E2379_03' ''
                            }
                        }
                    }
                    if (currentParty?.ChargePaymentInstruction?.ChargeCategoryCode || currentParty?.ChargePaymentInstruction?.PrepaidCollectIndicator){
                        'Group14_CPI' {
                            'CPI' {
                                'C229_01' {
                                    'E5237_01' currentParty?.ChargePaymentInstruction?.ChargeCategoryCode
                                    'E1131_02' ''
                                    'E3055_03' ''
                                }
                                'C231_02' {
                                    'E4215_01' ''
                                    'E1131_02' ''
                                    'E3055_03' ''
                                }
                                'E4237_03' currentParty?.ChargePaymentInstruction?.PrepaidCollectIndicator
                            }
                            'RFF' {
                                'C506_01' {
                                    'E1153_01' ''
                                    'E1154_02' ''
                                    'E1156_03' ''
                                    'E4000_04' ''
                                    'E1060_05' ''
                                }
                            }
                            'CUX' {
                                'C504_01' {
                                    'E6347_01' ''
                                    'E6345_02' ''
                                    'E6343_03' ''
                                    'E6348_04' ''
                                }
                                'C504_02' {
                                    'E6347_01' ''
                                    'E6345_02' ''
                                    'E6343_03' ''
                                    'E6348_04' ''
                                }
                                'E5402_03' ''
                                'E6341_04' ''
                            }
                            if (currentParty?.ChargePaymentInstruction?.PaymentLocation){
                                def chargePaymentInstruction = currentParty?.ChargePaymentInstruction?.first()
                                'LOC' {
                                    'E3227_01' '57'
                                    'C517_02' {
                                        if (chargePaymentInstruction?.PaymentLocation?.LocationCode?.UNLocationCode){
                                            'E3225_01' chargePaymentInstruction?.PaymentLocation?.LocationCode?.UNLocationCode
                                            'E1131_02' '181'
                                            'E3055_03' '6'
                                        }
                                        'E3224_04' chargePaymentInstruction?.PaymentLocation?.LocationName
                                    }
                                    'C519_03' {
                                        if (chargePaymentInstruction?.PaymentLocation?.LocationCode?.UNLocationCode){
                                            'E3223_01' util.getCS2MasterCityByUNLocCityType(chargePaymentInstruction?.PaymentLocation?.LocationCode?.UNLocationCode, 'S', conn)
                                            'E1131_02' '162'
                                            'E3055_03' '5'
                                        }
                                        'E3222_04' chargePaymentInstruction?.PaymentLocation?.Country
                                    }
                                    'C553_04' {
                                        'E3233_01' ''
                                        'E1131_02' ''
                                        'E3055_03' ''
                                        'E3232_04' ''
                                    }
                                    'E5479_05' ''
                                }
                            }
                            'MOA' {
                                'C516_01' {
                                    'E5025_01' ''
                                    'E5004_02' ''
                                    'E6345_03' ''
                                    'E6343_04' ''
                                    'E4405_05' ''
                                }
                            }
                        }
                    }
                    'Group15_TSR' {
                        'TSR' {
                            'C536_01' {
                                'E4065_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                            }
                            'C233_02' {
                                'E7273_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                                'E7273_04' ''
                                'E1131_05' ''
                                'E3055_06' ''
                            }
                            'C537_03' {
                                'E4219_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                            }
                            'C703_04' {
                                'E7085_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                            }
                        }
                        'RFF' {
                            'C506_01' {
                                'E1153_01' ''
                                'E1154_02' ''
                                'E1156_03' ''
                                'E4000_04' ''
                                'E1060_05' ''
                            }
                        }
                        'LOC' {
                            'E3227_01' ''
                            'C517_02' {
                                'E3225_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                                'E3224_04' ''
                            }
                            'C519_03' {
                                'E3223_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                                'E3222_04' ''
                            }
                            'C553_04' {
                                'E3233_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                                'E3232_04' ''
                            }
                            'E5479_05' ''
                        }
                        'TPL' {
                            'C222_01' {
                                'E8213_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                                'E8212_04' ''
                                'E8453_05' ''
                            }
                        }
                        'FTX' {
                            'E4451_01' ''
                            'E4453_02' ''
                            'C107_03' {
                                'E4441_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                            }
                            'C108_04' {
                                'E4440_01' ''
                                'E4440_02' ''
                                'E4440_03' ''
                                'E4440_04' ''
                                'E4440_05' ''
                            }
                            'E3453_05' ''
                            'E4447_06' ''
                        }
                    }
                }
            }
            if (currentBody.GeneralInformation.ActionType.take(3)!= 'CAN'){
                currentBody.Cargo.eachWithIndex { currentCargo, currentCargoIndex ->
                    'Group16_GID' {
                        'GID' {
                            'E1496_01' currentCargoIndex + 1
                            'C213_02' {
                                //todo exception for "Package Count Value is invalid"
                                'E7224_01' currentCargo.CargoInfo?.Packaging?.PackageQty
                                'E7065_02' getCarrierPackageCode(currentCargo?.CargoInfo?.Packaging?.PackageType)
                                if (currentCargo.CargoInfo?.Packaging?.PackageQty) {
                                    'E1131_03' '67'
                                    'E3055_04' '6'
                                }
                                'E7064_05' ''
                                'E7233_06' ''
                            }
                            'C213_03' {
                                'E7224_01' ''
                                'E7065_02' ''
                                'E1131_03' ''
                                'E3055_04' ''
                                'E7064_05' ''
                                'E7233_06' ''
                            }
                            'C213_04' {
                                'E7224_01' ''
                                'E7065_02' ''
                                'E1131_03' ''
                                'E3055_04' ''
                                'E7064_05' ''
                                'E7233_06' ''
                            }
                            'C213_05' {
                                'E7224_01' ''
                                'E7065_02' ''
                                'E1131_03' ''
                                'E3055_04' ''
                                'E7064_05' ''
                                'E7233_06' ''
                            }
                            'C213_06' {
                                'E7224_01' ''
                                'E7065_02' ''
                                'E1131_03' ''
                                'E3055_04' ''
                                'E7064_05' ''
                                'E7233_06' ''
                            }
                        }
                        'HAN' {
                            'C524_01' {
                                'E4079_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                                'E4078_04' ''
                            }
                            'C218_02' {
                                'E7419_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                                'E7418_04' ''
                            }
                        }
                        'TMP' {
                            'E6245_01' ''
                            'C239_02' {
                                'E6246_01' ''
                                'E6411_02' ''
                            }
                        }
                        'RNG' {
                            'E6167_01' ''
                            'C280_02' {
                                'E6411_01' ''
                                'E6162_02' ''
                                'E6152_03' ''
                            }
                        }
                        'TMD' {
                            'C219_01' {
                                'E8335_01' ''
                                'E8334_02' ''
                            }
                            'E8332_02' ''
                            'E8341_03' ''
                        }
                        'LOC' {
                            'E3227_01' ''
                            'C517_02' {
                                'E3225_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                                'E3224_04' ''
                            }
                            'C519_03' {
                                'E3223_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                                'E3222_04' ''
                            }
                            'C553_04' {
                                'E3233_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                                'E3232_04' ''
                            }
                            'E5479_05' ''
                        }
                        'MOA' {
                            'C516_01' {
                                'E5025_01' ''
                                'E5004_02' ''
                                'E6345_03' ''
                                'E6343_04' ''
                                'E4405_05' ''
                            }
                        }
                        'PIA' {
                            'E4347_01' ''
                            'C212_02' {
                                'E7140_01' ''
                                'E7143_02' ''
                                'E1131_03' ''
                                'E3055_04' ''
                            }
                            'C212_03' {
                                'E7140_01' ''
                                'E7143_02' ''
                                'E1131_03' ''
                                'E3055_04' ''
                            }
                            'C212_04' {
                                'E7140_01' ''
                                'E7143_02' ''
                                'E1131_03' ''
                                'E3055_04' ''
                            }
                            'C212_05' {
                                'E7140_01' ''
                                'E7143_02' ''
                                'E1131_03' ''
                                'E3055_04' ''
                            }
                            'C212_06' {
                                'E7140_01' ''
                                'E7143_02' ''
                                'E1131_03' ''
                                'E3055_04' ''
                            }
                        }
                        'FTX' {
                            'E4451_01' ''
                            'E4453_02' ''
                            'C107_03' {
                                'E4441_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                            }
                            'C108_04' {
                                'E4440_01' ''
                                'E4440_02' ''
                                'E4440_03' ''
                                'E4440_04' ''
                                'E4440_05' ''
                            }
                            'E3453_05' ''
                            'E4447_06' ''
                        }
                        'PCD' {
                            'C501_01' {
                                'E5245_01' ''
                                'E5482_02' ''
                                'E5249_03' ''
                                'E1131_04' ''
                                'E3055_05' ''
                            }
                        }
                        'Group17_NAD' {
                            'NAD' {
                                'E3035_01' ''
                                'C082_02' {
                                    'E3039_01' ''
                                    'E1131_02' ''
                                    'E3055_03' ''
                                }
                                'C058_03' {
                                    'E3124_01' ''
                                    'E3124_02' ''
                                    'E3124_03' ''
                                    'E3124_04' ''
                                    'E3124_05' ''
                                }
                                'C080_04' {
                                    'E3036_01' ''
                                    'E3036_02' ''
                                    'E3036_03' ''
                                    'E3036_04' ''
                                    'E3036_05' ''
                                    'E3045_06' ''
                                }
                                'C059_05' {
                                    'E3042_01' ''
                                    'E3042_02' ''
                                    'E3042_03' ''
                                    'E3042_04' ''
                                }
                                'E3164_06' ''
                                'C819_07' {
                                    'E3229_01' ''
                                    'E1131_02' ''
                                    'E3055_03' ''
                                    'E3228_04' ''
                                }
                                'E3251_08' ''
                                'E3207_09' ''
                            }
                            'DTM' {
                                'C507_01' {
                                    'E2005_01' ''
                                    'E2380_02' ''
                                    'E2379_03' ''
                                }
                            }
                        }
                        'GDS' {
                            'C703_01' {
                                'E7085_01' ''
                                'E1131_02' ''
                                'E3055_03' ''
                            }
                        }
                        'Group18_MEA' {
                            'MEA' {
                                'E6311_01' ''
                                'C502_02' {
                                    'E6313_01' ''
                                    'E6321_02' ''
                                    'E6155_03' ''
                                    'E6154_04' ''
                                }
                                'C174_03' {
                                    'E6411_01' ''
                                    'E6314_02' ''
                                    'E6162_03' ''
                                    'E6152_04' ''
                                    'E6432_05' ''
                                }
                                'E7383_04' ''
                            }
                            'EQN' {
                                'C523_01' {
                                    'E6350_01' ''
                                    'E6353_02' ''
                                }
                            }
                        }
                        'Group19_DIM' {
                            'DIM' {
                                'E6145_01' ''
                                'C211_02' {
                                    'E6411_01' ''
                                    'E6168_02' ''
                                    'E6140_03' ''
                                    'E6008_04' ''
                                }
                            }
                            'EQN' {
                                'C523_01' {
                                    'E6350_01' ''
                                    'E6353_02' ''
                                }
                            }
                        }
                        'Group20_RFF' {
                            'RFF' {
                                'C506_01' {
                                    'E1153_01' ''
                                    'E1154_02' ''
                                    'E1156_03' ''
                                    'E4000_04' ''
                                    'E1060_05' ''
                                }
                            }
                            'DTM' {
                                'C507_01' {
                                    'E2005_01' ''
                                    'E2380_02' ''
                                    'E2379_03' ''
                                }
                            }
                        }
                        'Group21_PCI' {
                            'PCI' {
                                'E4233_01' ''
                                'C210_02' {
                                    'E7102_01' ''
                                    'E7102_02' ''
                                    'E7102_03' ''
                                    'E7102_04' ''
                                    'E7102_05' ''
                                    'E7102_06' ''
                                    'E7102_07' ''
                                    'E7102_08' ''
                                    'E7102_09' ''
                                    'E7102_10' ''
                                }
                                'E8275_03' ''
                                'C827_04' {
                                    'E7511_01' ''
                                    'E1131_02' ''
                                    'E3055_03' ''
                                }
                            }
                            'RFF' {
                                'C506_01' {
                                    'E1153_01' ''
                                    'E1154_02' ''
                                    'E1156_03' ''
                                    'E4000_04' ''
                                    'E1060_05' ''
                                }
                            }
                            'DTM' {
                                'C507_01' {
                                    'E2005_01' ''
                                    'E2380_02' ''
                                    'E2379_03' ''
                                }
                            }
                            'GIN' {
                                'E7405_01' ''
                                'C208_02' {
                                    'E7402_01' ''
                                    'E7402_02' ''
                                }
                                'C208_03' {
                                    'E7402_01' ''
                                    'E7402_02' ''
                                }
                                'C208_04' {
                                    'E7402_01' ''
                                    'E7402_02' ''
                                }
                                'C208_05' {
                                    'E7402_01' ''
                                    'E7402_02' ''
                                }
                                'C208_06' {
                                    'E7402_01' ''
                                    'E7402_02' ''
                                }
                            }
                        }
                        'Group22_DOC' {
                            'DOC' {
                                'C002_01' {
                                    'E1001_01' ''
                                    'E1131_02' ''
                                    'E3055_03' ''
                                    'E1000_04' ''
                                }
                                'C503_02' {
                                    'E1004_01' ''
                                    'E1373_02' ''
                                    'E1366_03' ''
                                    'E3453_04' ''
                                    'E1056_05' ''
                                    'E1060_06' ''
                                }
                                'E3153_03' ''
                                'E1220_04' ''
                                'E1218_05' ''
                            }
                            'DTM' {
                                'C507_01' {
                                    'E2005_01' ''
                                    'E2380_02' ''
                                    'E2379_03' ''
                                }
                            }
                        }
                        'Group23_TPL' {
                            'TPL' {
                                'C222_01' {
                                    'E8213_01' ''
                                    'E1131_02' ''
                                    'E3055_03' ''
                                    'E8212_04' ''
                                    'E8453_05' ''
                                }
                            }
                            'Group24_MEA' {
                                'MEA' {
                                    'E6311_01' ''
                                    'C502_02' {
                                        'E6313_01' ''
                                        'E6321_02' ''
                                        'E6155_03' ''
                                        'E6154_04' ''
                                    }
                                    'C174_03' {
                                        'E6411_01' ''
                                        'E6314_02' ''
                                        'E6162_03' ''
                                        'E6152_04' ''
                                        'E6432_05' ''
                                    }
                                    'E7383_04' ''
                                }
                                'EQN' {
                                    'C523_01' {
                                        'E6350_01' ''
                                        'E6353_02' ''
                                    }
                                }
                            }
                        }
                        'Group25_SGP' {
                            'SGP' {
                                'C237_01' {
                                    'E8260_01' ''
                                    'E1131_02' ''
                                    'E3055_03' ''
                                    'E3207_04' ''
                                }
                                'E7224_02' ''
                            }
                            'Group26_MEA' {
                                'MEA' {
                                    'E6311_01' ''
                                    'C502_02' {
                                        'E6313_01' ''
                                        'E6321_02' ''
                                        'E6155_03' ''
                                        'E6154_04' ''
                                    }
                                    'C174_03' {
                                        'E6411_01' ''
                                        'E6314_02' ''
                                        'E6162_03' ''
                                        'E6152_04' ''
                                        'E6432_05' ''
                                    }
                                    'E7383_04' ''
                                }
                                'EQN' {
                                    'C523_01' {
                                        'E6350_01' ''
                                        'E6353_02' ''
                                    }
                                }
                            }
                        }
                        'Group27_DGS' {
                            'DGS' {
                                'E8273_01' ''
                                'C205_02' {
                                    'E8351_01' ''
                                    'E8078_02' ''
                                    'E8092_03' ''
                                }
                                'C234_03' {
                                    'E7124_01' ''
                                    'E7088_02' ''
                                }
                                'C223_04' {
                                    'E7106_01' ''
                                    'E6411_02' ''
                                }
                                'E8339_05' ''
                                'E8364_06' ''
                                'E8410_07' ''
                                'E8126_08' ''
                                'C235_09' {
                                    'E8158_01' ''
                                    'E8186_02' ''
                                }
                                'C236_10' {
                                    'E8246_01' ''
                                    'E8246_02' ''
                                    'E8246_03' ''
                                }
                                'E8255_11' ''
                                'E8325_12' ''
                                'E8211_13' ''
                            }
                            'FTX' {
                                'E4451_01' ''
                                'E4453_02' ''
                                'C107_03' {
                                    'E4441_01' ''
                                    'E1131_02' ''
                                    'E3055_03' ''
                                }
                                'C108_04' {
                                    'E4440_01' ''
                                    'E4440_02' ''
                                    'E4440_03' ''
                                    'E4440_04' ''
                                    'E4440_05' ''
                                }
                                'E3453_05' ''
                                'E4447_06' ''
                            }
                            'Group28_CTA' {
                                'CTA' {
                                    'E3139_01' ''
                                    'C056_02' {
                                        'E3413_01' ''
                                        'E3412_02' ''
                                    }
                                }
                                'COM' {
                                    'C076_01' {
                                        'E3148_01' ''
                                        'E3155_02' ''
                                    }
                                }
                            }
                            'Group29_MEA' {
                                'MEA' {
                                    'E6311_01' ''
                                    'C502_02' {
                                        'E6313_01' ''
                                        'E6321_02' ''
                                        'E6155_03' ''
                                        'E6154_04' ''
                                    }
                                    'C174_03' {
                                        'E6411_01' ''
                                        'E6314_02' ''
                                        'E6162_03' ''
                                        'E6152_04' ''
                                        'E6432_05' ''
                                    }
                                    'E7383_04' ''
                                }
                                'EQN' {
                                    'C523_01' {
                                        'E6350_01' ''
                                        'E6353_02' ''
                                    }
                                }
                            }
                            'Group30_SGP' {
                                'SGP' {
                                    'C237_01' {
                                        'E8260_01' ''
                                        'E1131_02' ''
                                        'E3055_03' ''
                                        'E3207_04' ''
                                    }
                                    'E7224_02' ''
                                }
                                'Group31_MEA' {
                                    'MEA' {
                                        'E6311_01' ''
                                        'C502_02' {
                                            'E6313_01' ''
                                            'E6321_02' ''
                                            'E6155_03' ''
                                            'E6154_04' ''
                                        }
                                        'C174_03' {
                                            'E6411_01' ''
                                            'E6314_02' ''
                                            'E6162_03' ''
                                            'E6152_04' ''
                                            'E6432_05' ''
                                        }
                                        'E7383_04' ''
                                    }
                                    'EQN' {
                                        'C523_01' {
                                            'E6350_01' ''
                                            'E6353_02' ''
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
            'Group32_EQD' {
                'EQD' {
                    'E8053_01' ''
                    'C237_02' {
                        'E8260_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E3207_04' ''
                    }
                    'C224_03' {
                        'E8155_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E8154_04' ''
                    }
                    'E8077_04' ''
                    'E8249_05' ''
                    'E8169_06' ''
                }
                'EQN' {
                    'C523_01' {
                        'E6350_01' ''
                        'E6353_02' ''
                    }
                }
                'TMD' {
                    'C219_01' {
                        'E8335_01' ''
                        'E8334_02' ''
                    }
                    'E8332_02' ''
                    'E8341_03' ''
                }
                'MEA' {
                    'E6311_01' ''
                    'C502_02' {
                        'E6313_01' ''
                        'E6321_02' ''
                        'E6155_03' ''
                        'E6154_04' ''
                    }
                    'C174_03' {
                        'E6411_01' ''
                        'E6314_02' ''
                        'E6162_03' ''
                        'E6152_04' ''
                        'E6432_05' ''
                    }
                    'E7383_04' ''
                }
                'DIM' {
                    'E6145_01' ''
                    'C211_02' {
                        'E6411_01' ''
                        'E6168_02' ''
                        'E6140_03' ''
                        'E6008_04' ''
                    }
                }
                'TPL' {
                    'C222_01' {
                        'E8213_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E8212_04' ''
                        'E8453_05' ''
                    }
                }
                'HAN' {
                    'C524_01' {
                        'E4079_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E4078_04' ''
                    }
                    'C218_02' {
                        'E7419_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                        'E7418_04' ''
                    }
                }
                'TMP' {
                    'E6245_01' ''
                    'C239_02' {
                        'E6246_01' ''
                        'E6411_02' ''
                    }
                }
                'FTX' {
                    'E4451_01' ''
                    'E4453_02' ''
                    'C107_03' {
                        'E4441_01' ''
                        'E1131_02' ''
                        'E3055_03' ''
                    }
                    'C108_04' {
                        'E4440_01' ''
                        'E4440_02' ''
                        'E4440_03' ''
                        'E4440_04' ''
                        'E4440_05' ''
                    }
                    'E3453_05' ''
                    'E4447_06' ''
                }
                'RFF' {
                    'C506_01' {
                        'E1153_01' ''
                        'E1154_02' ''
                        'E1156_03' ''
                        'E4000_04' ''
                        'E1060_05' ''
                    }
                }
                'Group33_NAD' {
                    'NAD' {
                        'E3035_01' ''
                        'C082_02' {
                            'E3039_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                        }
                        'C058_03' {
                            'E3124_01' ''
                            'E3124_02' ''
                            'E3124_03' ''
                            'E3124_04' ''
                            'E3124_05' ''
                        }
                        'C080_04' {
                            'E3036_01' ''
                            'E3036_02' ''
                            'E3036_03' ''
                            'E3036_04' ''
                            'E3036_05' ''
                            'E3045_06' ''
                        }
                        'C059_05' {
                            'E3042_01' ''
                            'E3042_02' ''
                            'E3042_03' ''
                            'E3042_04' ''
                        }
                        'E3164_06' ''
                        'C819_07' {
                            'E3229_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                            'E3228_04' ''
                        }
                        'E3251_08' ''
                        'E3207_09' ''
                    }
                    'DTM' {
                        'C507_01' {
                            'E2005_01' ''
                            'E2380_02' ''
                            'E2379_03' ''
                        }
                    }
                    'Group34_CTA' {
                        'CTA' {
                            'E3139_01' ''
                            'C056_02' {
                                'E3413_01' ''
                                'E3412_02' ''
                            }
                        }
                        'COM' {
                            'C076_01' {
                                'E3148_01' ''
                                'E3155_02' ''
                            }
                        }
                    }
                }
                'Group35_DGS' {
                    'DGS' {
                        'E8273_01' ''
                        'C205_02' {
                            'E8351_01' ''
                            'E8078_02' ''
                            'E8092_03' ''
                        }
                        'C234_03' {
                            'E7124_01' ''
                            'E7088_02' ''
                        }
                        'C223_04' {
                            'E7106_01' ''
                            'E6411_02' ''
                        }
                        'E8339_05' ''
                        'E8364_06' ''
                        'E8410_07' ''
                        'E8126_08' ''
                        'C235_09' {
                            'E8158_01' ''
                            'E8186_02' ''
                        }
                        'C236_10' {
                            'E8246_01' ''
                            'E8246_02' ''
                            'E8246_03' ''
                        }
                        'E8255_11' ''
                        'E8325_12' ''
                        'E8211_13' ''
                    }
                    'FTX' {
                        'E4451_01' ''
                        'E4453_02' ''
                        'C107_03' {
                            'E4441_01' ''
                            'E1131_02' ''
                            'E3055_03' ''
                        }
                        'C108_04' {
                            'E4440_01' ''
                            'E4440_02' ''
                            'E4440_03' ''
                            'E4440_04' ''
                            'E4440_05' ''
                        }
                        'E3453_05' ''
                        'E4447_06' ''
                    }
                    'Group36_CTA' {
                        'CTA' {
                            'E3139_01' ''
                            'C056_02' {
                                'E3413_01' ''
                                'E3412_02' ''
                            }
                        }
                        'COM' {
                            'C076_01' {
                                'E3148_01' ''
                                'E3155_02' ''
                            }
                        }
                    }
                }
            }
            'UNT' {
                'E0074_01' '1'
                'E0062_02' '1'
            }
        }
    }


    String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {

        /**
         * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
         */
        inputXmlBody = util.removeBOM(inputXmlBody)

        /**
         * Part II: get  mapping runtime parameters
         */
//		[]{"B2BSessionID="+B2BSessionID, "B2B_OriginalSourceFileName="+B2B_OriginalSourceFileName, "B2B_SendPortID="+B2B_SendPortID, "PortProperty="+PortProperty, "MSG_REQ_ID="+MSG_REQ_ID, "TP_ID="+TP_ID, "MSG_TYPE_ID="+MSG_TYPE_ID, "DIR_ID="+DIR_ID};
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
        //parse input cs2 br xml
        XmlBeanParser parser = new XmlBeanParser()
        def br = parser.xmlParser(util.cleanXml(inputXmlBody), BookingRequest.class)

        //prepare output
        def writer = new StringWriter()
        def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
        outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

        //prepare bizkey
        def bizKeyWriter = new StringWriter();
        def bizKeyXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(bizKeyWriter), "", false));
        bizKeyXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

        /**
         * Part IV: mapping script start from here
         */

        //Begin work flow
        TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
        currentSystemDt = new Date()
//		def headerMsgDT = util.convertDateTime(br.Header.MsgDT.LocDT, "yyyy-MM-dd'T'HH:mm:ss", 'yyyyMMddHHmmss')
//		def txnErrorKeys = []

        //Start mapping
//		dataSource= br.Header.DataSource
//		println(br.Request.GeneralInformation.SCAC)
        dataSource = br.Header.DataSource
        outXml.'IFTMBF' {
            br?.Request?.each { currentBody ->

                generateBody(currentBody, outXml)

            }
//			br?.Request?.each { currentBody ->
//
//				//associate container and cargo
//				//			Map<cs.b2b.core.mapping.bean.bc.Container, List<cs.b2b.core.mapping.bean.bc.Cargo>> associateContainerAndCargo = bcUtil.associateContainerAndCargo(current_Body)
//
////				List<Map<String,String>> errorKeyList = new ArrayList<Map<String,String>>()
//				//prep checking
////				prepValidation()
//
//				//mapping
//
//
//				// posp checking
////				if(errorKeyList.isEmpty()){
////					pospValidation()
////				}
//
////				bcUtil.buildBizKey()
//
////				txnErrorKeys.add(errorKeyList)
//			}
        }

        //End root node
//		outXml.nodeCompleted(null,T301)
//		bizKeyXml.nodeCompleted(null,bizKeyRoot)

//		bcUtil.promoteBizKeyToSession(appSessionId, bizKeyWriter);
//		bcUtil.promoteHeaderIntChgMsgId(appSessionId, br.Header?.InterchangeMessageID);
//		if (br.Body && br.Body.size()>0) {
//			bcUtil.promoteScacCode(appSessionId, br.Body[0].GeneralInformation?.SCACCode);
//		}

//		String result = '';
//		if (txnErrorKeys.size() != 0) {
//			result = util.cleanXml(writer?.toString())
//		}
        writer.close();

        return util.cleanXml(writer?.toString());
    }

    void prepValidation() {

    }

    void pospValidation() {

    }

    String constructContactNumber(ContactPhone contact) {
        String ret = ''
        if (contact.CountryCode) {
            ret = ret + contact.countryCode
        }
        if (contact.areaCode != null && contact.areaCode != '') {
            ret = (ret == '' ? ret : ret + '-') + contact.areaCode
        }
        if (contact.number != null && contact.number != '') {
            ret = (ret == '' ? ret : ret + '-') + contact.number
        }
        ret
    }

    String constructContactNumber(ContactFax contact) {
        ContactPhone contactPhoneMock = new ContactPhone()
        contactPhoneMock.CountryCode = contact.CountryCode
        contactPhoneMock.AreaCode = contact.AreaCode
        contactPhoneMock.Number = contact.Number
        constructContactNumber(contactPhoneMock)
    }

    String convertXmlDateToEdiDate(def xmlDateTime, String outFormat) {
        Calendar dateTime = DatatypeConverter.parseDateTime(xmlDateTime.toString())
        SimpleDateFormat sfmt = new SimpleDateFormat(outFormat);
        sfmt.format(dateTime.getTime())
    }

    String convertUnLocationCode(unLocationCode){
        return unLocationCode?util.getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'UNLocCdeConversion', unLocationCode.toString(),unLocationCode.toString(), conn):null
    }

    String convertCsCompanyIdtoCCC(csCompanyId){
        return csCompanyId?util.getConversionWithDefault(TP_ID, MSG_TYPE_ID, DIR_ID, 'CarrierCustCode', csCompanyId,'', conn):''
    }

    List splitString(def inputObject, int maxCharPerLine) {
        String inputString = inputObject.toString()
        if (inputString == '') {
            return null
        }

        if (maxCharPerLine < 1) {
            return null
        }
        List lines = new LinkedList()
        while (util.notEmpty(inputString)) {
            if (inputString.length() > maxCharPerLine) {
                if (inputString[maxCharPerLine] == ' '){
                    lines.add(inputString.take(maxCharPerLine).toString().trim())
                    inputString = inputString.substring(maxCharPerLine).trim()
                } else {
                    int splitPosistion = inputString.take(maxCharPerLine).toString().lastIndexOf(" ")
                    if (splitPosistion != -1) {
                        lines.add(inputString.take(splitPosistion))
                        inputString = inputString.substring(splitPosistion).trim()
                    } else {
                        lines.add(inputString.take(maxCharPerLine))
                        inputString = inputString.substring(maxCharPerLine).trim()
                    }
                }
            } else {
                lines.add(inputString)
                inputString = ''
            }
        }
        lines
    }

    String getCarrierPackageCode(String packageType){
        util.getConversion(TP_ID,MSG_TYPE_ID,DIR_ID, 'PackageType', packageType,conn)
    }

    String getCarrierPackageDesc(String packageType){
        def ret = util.getCdeConversionImpl(TP_ID,MSG_TYPE_ID,DIR_ID,)
    }
}
