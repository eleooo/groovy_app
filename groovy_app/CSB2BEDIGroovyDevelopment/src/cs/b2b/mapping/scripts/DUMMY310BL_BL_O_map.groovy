package cs.b2b.mapping.scripts

import com.sun.xml.internal.fastinfoset.util.StringArray
import groovy.util.XmlSlurper
import groovy.xml.MarkupBuilder
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil
import org.w3c.dom.CDATASection

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/**
 * IG		: BL CS2 310
 * Version	: 0.8
 */

String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {


    cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();

    /**
     * Part I: prep handling here, remove XML BOM flag in file beginning, customer sample contains it
     */
    inputXmlBody = util.removeBOM(inputXmlBody)

    /**
     * Part II: get app mapping runtime parameters
     */
    def appSessionId = util.getRuntimeParameter("AppSessionID", runtimeParams);
    def sourceFileName = util.getRuntimeParameter("OriginalSourceFileName", runtimeParams);
    //pmt info
    def TP_ID = util.getRuntimeParameter("TP_ID", runtimeParams);
    def MSG_TYPE_ID = util.getRuntimeParameter("MSG_TYPE_ID", runtimeParams);
    def DIR_ID = util.getRuntimeParameter("DIR_ID", runtimeParams);
    def MSG_REQ_ID = util.getRuntimeParameter("MSG_REQ_ID", runtimeParams);

    /**
     * Part III: read xml and prepare output xml
     */
    //Important: the inputXml is xml root element
    def inputBLCS2XML = new XmlSlurper().parseText(inputXmlBody);

    def writer = new StringWriter()
    def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
    //new MarkupBuilder(writer) //new IndentPrinter(new PrintWriter(writer), "", false) - no fine print
    outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")

    /**
     * Part IV: mapping script start from here
     */



    outXml."Translation" {
        "TpId" TP_ID

        String AcceptedChargeType = "Collect"; //Collect/Prepaid/Both
        List AcceptedFreightCharge;

        inputBLCS2XML.Body.eachWithIndex {
            body, bodyIndex ->

                String vBLNumber = body.GeneralInformation?.BLNumber?.text()
                String vBookingNum = body.GeneralInformation?.CarrierBookingNumber[0]?.text()
                String vScacCode = body.GeneralInformation?.SCACCode?.text()
                String vSIDNum = body.ExternalReference.findAll {
                    it.CSReferenceType.text() == "SID"
                }.ReferenceNumber.text()

                //DateTime
                String vActualArrivalAtFinalHubLocDT = body.Route?.ArrivalAtFinalHub.find {
                    it.@Indicator == "A"
                }.LocDT.text()
                String vEstimateArrivalAtFinalHubLocDT = body.Route?.ArrivalAtFinalHub.find {
                    it.@Indicator == "E"
                }.LocDT.text()

                //Last OceanLeg
                String vActualLastOceanLegArrivalDTLocDT = body.Route?.OceanLeg[body.Route.OceanLeg.size() - 1].ArrivalDT.find {
                    it.@Indicator == "A"
                }.LocDT.text()
                String vEstimateLastOceanLegArrivalDTLocDT = body.Route?.OceanLeg[body.Route.OceanLeg.size() - 1].ArrivalDT.find {
                    it.@Indicator == "E"
                }.LocDT.text()

                String vBLIssueDateLocDT = body.GeneralInformation?.BLIssueDT?.LocDT?.text()

                //First OceanLeg
                String vActualFirstOceanLegDepatureDTLocDt = body.Route?.OceanLeg[0]?.DepartureDT?.findAll {
                    it.@Indicator == "A"
                }?.LocDT?.text()
                String vEstimateFirstOceanLegDepatureDTLocDt = body.Route?.OceanLeg[0]?.DepartureDT?.findAll {
                    it.@Indicator == "E"
                }?.LocDT?.text()



                String vActualFullReturnCutoffDTLocDt = body.Route.FullReturnCutoffDT.LocDT.text()


                outXml."Consolidated-Transactions" {
                    outXml."T-310" {
                        outXml."S-ST_1" {
                            "E-143_1_01" "310"
                            "E-329_1_02" "-999"
                        }

                        outXml."S-B3_2" {
                            if (body.GeneralInformation?.BLType?.text().toUpperCase() == "ORIGINAL") {
                                "E-147_2_01" "B"
                            } else if (body.GeneralInformation?.BLType?.text().toUpperCase() == "SEA WAYBILL") {
                                "E-147_2_01" "2"
                            }

                            "E-76_2_02" vBLNumber

                            if (vSIDNum) {
                                "E-145_2_03" vSIDNum
                            }

                            "E-146_2_04"
                            if (body.GeneralInformation.BLOnboardDT.GMT) {
                                "E-373_2_06" util.convertDateTime(body.GeneralInformation?.BLOnboardDT?.GMT?.text(), "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                            }

                            "E-193_2_07"

                            if (vActualArrivalAtFinalHubLocDT != "") {
                                "E-32_2_09" util.convertDateTime(vActualArrivalAtFinalHubLocDT, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                                "E-374_2_10" "140"
                            } else if (vEstimateArrivalAtFinalHubLocDT != "") {
                                "E-32_2_09" util.convertDateTime(vEstimateArrivalAtFinalHubLocDT, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                                "E-374_2_10" "139"
                            } else if (vActualLastOceanLegArrivalDTLocDT != "") {
                                "E-32_2_09" util.convertDateTime(vActualLastOceanLegArrivalDTLocDT, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                                "E-374_2_10" "140"
                            } else if (vEstimateLastOceanLegArrivalDTLocDT != "") {
                                "E-32_2_09" util.convertDateTime(vEstimateLastOceanLegArrivalDTLocDT, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                                "E-374_2_10" "139"
                            }

                            "E-140_2_11" vScacCode
                            if (vBLIssueDateLocDT) {
                                "E-373_2_12" util.convertDateTime(vBLIssueDateLocDT, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                            }

                            if (body.Route.Haulage?.OutBound?.text() == "C") {
                                outboundCode = "D"
                            } else if (body.Route.Haulage?.OutBound?.text() == "M") {
                                outboundCode = "P"
                            }

                            if (body.Route.Haulage?.InBound?.text() == "C") {
                                inboundCode = "D"
                            } else if (body.Route.Haulage?.InBound?.text() == "M") {
                                inboundCode = "P"
                            }

                            "E-375_2_13" outboundCode.concat(inboundCode)
                        }

                        outXml."S-B2A_3" {
                            if (body.TransactionInformation?.Action?.text() == "NEW") {
                                "E-353_3_01" "00"
                            } else if (body.TransactionInformation?.Action?.text() == "UPD") {
                                "E-353_3_01" "05"
                            } else if (body.TransactionInformation?.Action?.text() == "DEL") {
                                "E-353_3_01" "01"
                            }
                        }

                        //BL Number
                        if (vBLNumber) {
                            outXml."S-N9_6" {
                                "E-128_6_01" "BM"
                                "E-127_6_02" vBLNumber
                            }
                        }

                        if (vBookingNum) {
                            outXml."S-N9_6" {
                                "E-128_6_01" "BN"
                                "E-127_6_02" vBookingNum
                            }
                        }

                        //External Reference
                        body.ExternalReference.each {
                            currentExternal ->
                                String vConvertType = util.getConversion(TP_ID, MSG_TYPE_ID, DIR_ID, "REFERENCECODECS2", currentExternal.CSReferenceType.text(), conn)
                                if (vConvertType) {
                                    outXml."S-N9_6" {
                                        "E-128_6_01" vConvertType
                                        "E-127_6_02" currentExternal.ReferenceNumber.text()
                                    }
                                }
                        }

                        //CarrierRate
                        body.CarrierRate.each {
                            currentCarrierRate ->
                                String vConvertType = util.getConversion(TP_ID, MSG_TYPE_ID, DIR_ID, "REFERENCECODECS2", currentCarrierRate.CSReferenceType.text(), conn)
                                if (vConvertType) {
                                    outXml."S-N9_6" {
                                        "E-128_6_01" vConvertType
                                        "E-127_6_02" currentCarrierRate.CarrierRateNumber.text()
                                    }
                                }
                        }

                        //LastOceanLeg
                        //Last Ocean Leg
                        String vLastDischargeLloydsNum;
                        String vLastDischargeCallSign;
                        String vLastDischargVesselName;
                        String vLastDischargeDirectionName;

                        body.Route.OceanLeg[body.Route.OceanLeg.size() - 1].each {
                            lastOceanLeg ->
                                vLastDischargeLloydsNum = lastOceanLeg?.SVVD?.Discharge?.LloydsNumber?.text()
                                vLastDischargeCallSign = lastOceanLeg?.SVVD?.Discharge?.CallSign?.text()
                                vLastDischargVesselName = lastOceanLeg?.SVVD?.Discharge?.VesselName?.text()
                                vLastDischargeDirectionName = lastOceanLeg?.SVVD?.Discharge?.Voyage?.text().concat(lastOceanLeg?.DischargeDirectionName?.text())
                        }

                        //When there only one OceanLeg
                        if (body.Route.OceanLeg.size() == 1) {
                            outXml."S-V1_7" {
                                if (vLastDischargeLloydsNum) {
                                    "E-597_7_01" vLastDischargeLloydsNum
                                } else if (vLastDischargeCallSign) {
                                    "E-597_7_01" vLastDischargeCallSign
                                }
                                "E-182_7_02" vLastDischargVesselName
                                "E-55_7_04" vLastDischargeDirectionName
                                "E-140_7_05" vScacCode
                                if (vLastDischargeLloydsNum) {
                                    "E-897_7_08" "L"
                                } else if (vLastDischargeCallSign) {
                                    "E-897_7_08" "C"
                                }
                            }
                            //More than one OceanLeg
                        } else if (body.Route.OceanLeg.size() > 1) {
                            //Prepare first
                            String vfirstLoadingLloydsNum;
                            String vfirstLoadingCallSign;
                            String vfirstLoadingVesselName;
                            String vfirstLoadingDirectionName;

                            body.Route.OceanLeg[0].each {
                                firstOceanLeg ->
                                    vfirstLoadingLloydsNum = firstOceanLeg?.SVVD?.Loading?.LloydsNumber?.text()
                                    vfirstLoadingCallSign = firstOceanLeg?.SVVD?.Loading?.CallSign?.text()
                                    vfirstLoadingVesselName = firstOceanLeg?.SVVD?.Loading?.VesselName?.text()
                                    vfirstLoadingDirectionName = firstOceanLeg?.SVVD?.Loading?.Voyage?.text().concat(firstOceanLeg?.LoadingDirectionName?.text())
                            }

                            outXml."S-V1_7" {
                                if (vfirstLoadingLloydsNum) {
                                    "E-597_7_01" vfirstLoadingLloydsNum
                                } else if (vfirstLoadingCallSign) {
                                    "E-597_7_01" vfirstLoadingCallSign
                                }
                                "E-182_7_02" vfirstLoadingVesselName
                                "E-55_7_04" vfirstLoadingDirectionName
                                "E-140_7_05" vScacCode
                                if (vfirstLoadingLloydsNum) {
                                    "E-897_7_08" "L"
                                } else if (vfirstLoadingCallSign) {
                                    "E-897_7_08" "C"
                                }
                            }

                            outXml."S-V1_7" {
                                if (vLastDischargeLloydsNum) {
                                    "E-597_7_01" vLastDischargeLloydsNum
                                } else if (vLastDischargeCallSign) {
                                    "E-597_7_01" vLastDischargeCallSign
                                }
                                "E-182_7_02" vLastDischargVesselName
                                "E-55_7_04" vLastDischargeDirectionName
                                "E-140_7_05" vScacCode
                                if (vLastDischargeLloydsNum) {
                                    "E-897_7_08" "L"
                                } else if (vLastDischargeCallSign) {
                                    "E-897_7_08" "C"
                                }
                            }

                        }

                        body.Party.each {
                            currentParty ->
                                String partyName = currentParty.PartyType.text()
                                if (partyName == "SHP" || partyName == "ANP" || partyName == "CAR" || partyName == "CGN" || partyName == "FWD" || partyName == "NPT") {
                                    outXml."L-N1_13" {
                                        outXml."S-N1_13" {
                                            if (partyName == "SHP") {
                                                "E-98_13_01" "SH"
                                            } else if (partyName == "ANP") {
                                                "E-98_13_01" "N2"
                                            } else if (partyName == "CAR") {
                                                "E-98_13_01" "CA"
                                            } else if (partyName == "CGN") {
                                                "E-98_13_01" "CN"
                                            } else if (partyName == "FWD") {
                                                "E-98_13_01" "FW"
                                            } else if (partyName == "NPT") {
                                                "E-98_13_01" "N1"
                                            }
                                            "E-93_13_02" currentParty.PartyName.text()
                                            if (currentParty.CarrierCustomerCode) {
                                                "E-66_13_03" "25"
                                                "E-67_13_04" currentParty.CarrierCustomerCode.text()
                                            }
                                        }

                                        StringBuffer sbAddressLine = new StringBuffer()
                                        currentParty.Address?.AddressLines?.AddressLine?.each {
                                            currentAddressLine ->
                                                if (currentAddressLine) {
                                                    sbAddressLine.append(currentAddressLine.text())
                                                    sbAddressLine.append(" ")
                                                }
                                        }
                                        if (sbAddressLine.toString() != "") {
                                            outXml."S-N3_15" {
                                                "E-166_15_01" commonSubstring(sbAddressLine.toString(), 0, 55)
                                                if (sbAddressLine.toString().length() > 56) {
                                                    "E-166_15_02" commonSubstring(sbAddressLine.toString(), 55, 55)
                                                }
                                            }
                                        }

                                        outXml."S-N4_16" {
                                            "E-19_16_01" currentParty.Address.City.text()
                                            "E-156_16_02" util.getStateCdeFromCS2MasterCity(currentParty.Address.State.text(), currentParty.Address.City.text(), currentParty.Address.Country.text(), conn)
                                            if (currentParty.Address.PostalCode.text()) {
                                                "E-116_16_03" currentParty.Address.PostalCode.text()
                                            }
                                            "E-26_16_04" currentParty.Address.Country.text()

                                            if (currentParty.Address.LocationCode.UNLocationCode) {
                                                "E-309_16_05" "UN"
                                                "E-310_16_06" currentParty.Address.LocationCode.UNLocationCode.text()
                                            }
                                        }
                                    }
                                }
                        }

                        outXml."L-N1_13" {
                            outXml."S-N1_13" {
                                "E-98_13_01" "CA"
                                "E-93_13_02" vScacCode
                            }
                        }

                        //Telephone Number
                        body.Party.each {
                            currentParty ->
                                String partyName = currentParty.PartyType.text()
                                String ConvertedPartyName;

                                if (partyName == "SHP") {
                                    ConvertedPartyName = "SH"
                                } else if (partyName == "ANP") {
                                    ConvertedPartyName = "N2"
                                } else if (partyName == "CAR") {
                                    ConvertedPartyName = "CA"
                                } else if (partyName == "CGN") {
                                    ConvertedPartyName = "CN"
                                } else if (partyName == "FWD") {
                                    ConvertedPartyName = "FW"
                                } else if (partyName == "NPT") {
                                    ConvertedPartyName = "N1"
                                }

                                String partyCountryCode = currentParty.Contact?.ContactPhone?.CountryCode.text()
                                String partyAreaCode = currentParty.Contact?.ContactPhone?.AreaCode.text()
                                String partyTelephone = currentParty.Contact?.ContactPhone?.Number.text()
                                String partyEmail = currentParty.Contact?.ContactEmailAddress.text()

                                if (currentParty.Contact?.FirstName != "") {
                                    if (partyTelephone != "") {
                                        outXml."S-G61_17" {
                                            "E-366_17_01" ConvertedPartyName
                                            "E-93_17_02" currentParty.Contact?.FirstName.text()
                                            "E-365_17_03" "TE"
                                            "E-364_17_04" ConcatphoneNumber(partyCountryCode, partyAreaCode, partyTelephone)
                                        }
                                    }

                                    if (partyEmail != "") {
                                        outXml."S-G61_17" {
                                            "E-366_17_01" ConvertedPartyName
                                            "E-93_17_02" currentParty.Contact?.FirstName.text()
                                            "E-365_17_03" "EM"
                                            "E-364_17_04" partyEmail
                                        }
                                    }
                                }
                        }

                        ["POR", "POL", "POD", "FND"].each {
                            currentRoute ->
                                if (currentRoute.equals("POR")) {
                                    outXml."L-R4_18" {
                                        outXml."S-R4_18" {
                                            "E-115_18_01" "R"
                                            if (body.Route.POR.CityDetails.LocationCode.UNLocationCode.text() != "") {
                                                "E-309_18_02" "UN"
                                                "E-310_18_03" body.Route.POR.CityDetails.LocationCode.UNLocationCode.text()
                                            }

                                            "E-114_18_04" body.Route.POR.CityDetails.City.text().toUpperCase()
                                            "E-26_18_05" body.Route.POR.CSStandardCity.CSCountryCode.text()
                                            "E-156_18_08" body.Route.POR.CSStandardCity.CSStateCode.text()
                                        }

                                        if (vActualFullReturnCutoffDTLocDt != "") {
                                            outXml."S-DTM_19" {
                                                "E-374_19_01" "140"
                                                "E-373_19_02" util.convertDateTime(vActualFullReturnCutoffDTLocDt, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                                                "E-337_19_03" util.convertDateTime(vActualFullReturnCutoffDTLocDt, "yyyy-MM-dd'T'HH:mm:ss", "HHmm")
                                            }
                                        }
                                    }
                                }


                                if (currentRoute.equals("POL")) {
                                    outXml."L-R4_18" {
                                        outXml."S-R4_18" {
                                            "E-115_18_01" "L"
                                            if (body.Route.FirstPOL.Port.LocationCode.UNLocationCode.text() != "") {
                                                "E-309_18_02" "UN"
                                                "E-310_18_03" body.Route.FirstPOL.Port.LocationCode.UNLocationCode.text()
                                            }
                                            "E-114_18_04" body.Route.FirstPOL.Port.PortName.text().toUpperCase()
                                            "E-26_18_05" body.Route.FirstPOL.Port.CSCountryCode.text()
                                            "E-156_18_08" body.Route.FirstPOL.CSStateCode.text()
                                        }
                                    }

                                    if (vActualFirstOceanLegDepatureDTLocDt != "") {
                                        outXml."S-DTM_19" {
                                            "E-374_19_01" "140"
                                            "E-373_19_02" util.convertDateTime(vActualFirstOceanLegDepatureDTLocDt, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                                            "E-337_19_03" util.convertDateTime(vActualFirstOceanLegDepatureDTLocDt, "yyyy-MM-dd'T'HH:mm:ss", "HHmm")
                                        }
                                    } else if (vEstimateFirstOceanLegDepatureDTLocDt != "") {
                                        outXml."S-DTM_19" {
                                            "E-374_19_01" "139"
                                            "E-373_19_02" util.convertDateTime(vEstimateFirstOceanLegDepatureDTLocDt, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                                            "E-337_19_03" util.convertDateTime(vEstimateFirstOceanLegDepatureDTLocDt, "yyyy-MM-dd'T'HH:mm:ss", "HHmm")
                                        }
                                    }
                                }


                                if (currentRoute.equals("POD")) {
                                    outXml."L-R4_18" {
                                        outXml."S-R4_18" {
                                            "E-115_18_01" "D"
                                            if (body.Route.LastPOD.Port.LocationCode.UNLocationCode.text() != "") {
                                                "E-309_18_02" "UN"
                                                "E-310_18_03" body.Route.LastPOD.Port.LocationCode.UNLocationCode.text()
                                            }
                                            "E-114_18_04" body.Route.LastPOD.Port.PortName.text().toUpperCase()
                                            "E-26_18_05" body.Route.LastPOD.Port.CSCountryCode.text()
                                            "E-156_18_08" body.Route.LastPOD.CSStateCode.text()
                                        }
                                    }

                                    if (vActualLastOceanLegArrivalDTLocDT != "") {
                                        outXml."S-DTM_19" {
                                            "E-374_19_01" "140"
                                            "E-373_19_02" util.convertDateTime(vActualLastOceanLegArrivalDTLocDT, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                                            "E-337_19_03" util.convertDateTime(vActualLastOceanLegArrivalDTLocDT, "yyyy-MM-dd'T'HH:mm:ss", "HHmm")
                                        }
                                    } else if (vEstimateLastOceanLegArrivalDTLocDT != "") {
                                        outXml."S-DTM_19" {
                                            "E-374_19_01" "139"
                                            "E-373_19_02" util.convertDateTime(vEstimateLastOceanLegArrivalDTLocDT, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                                            "E-337_19_03" util.convertDateTime(vEstimateLastOceanLegArrivalDTLocDT, "yyyy-MM-dd'T'HH:mm:ss", "HHmm")
                                        }
                                    }
                                }

                                if (currentRoute.equals("FND")) {
                                    outXml."L-R4_18" {
                                        outXml."S-R4_18" {
                                            "E-115_18_01" "E"
                                            if (body.Route.FND.CityDetails.LocationCode.UNLocationCode.text() != "") {
                                                "E-309_18_02" "UN"
                                                "E-310_18_03" body.Route.FND.CityDetails.LocationCode.UNLocationCode.text()
                                            }

                                            "E-114_18_04" body.Route.FND.CityDetails.City.text().toUpperCase()
                                            "E-26_18_05" body.Route.FND.CSStandardCity.CSCountryCode.text()
                                            "E-156_18_08" body.Route.FND.CSStandardCity.CSStateCode.text()
                                        }

                                        //MapActArrivalAtFinalHubLocDt
                                        //MapEstArrivalAtFinalHubLocDt
                                        //MapActArrivalDTLocDT
                                        //MapEstArrivalDTLocDT

                                        if (vActualArrivalAtFinalHubLocDT != "") {
                                            outXml."S-DTM_19" {
                                                "E-374_19_01" "140"
                                                "E-373_19_02" util.convertDateTime(vActualArrivalAtFinalHubLocDT, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                                                "E-337_19_03" util.convertDateTime(vActualArrivalAtFinalHubLocDT, "yyyy-MM-dd'T'HH:mm:ss", "HHmm")
                                            }
                                        } else if (vEstimateArrivalAtFinalHubLocDT != "") {
                                            outXml."S-DTM_19" {
                                                "E-374_19_01" "140"
                                                "E-373_19_02" util.convertDateTime(vEstimateArrivalAtFinalHubLocDT, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                                                "E-337_19_03" util.convertDateTime(vEstimateArrivalAtFinalHubLocDT, "yyyy-MM-dd'T'HH:mm:ss", "HHmm")
                                            }
                                        } else if (vActualLastOceanLegArrivalDTLocDT != "") {
                                            outXml."S-DTM_19" {
                                                "E-374_19_01" "140"
                                                "E-373_19_02" util.convertDateTime(vActualLastOceanLegArrivalDTLocDT, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                                                "E-337_19_03" util.convertDateTime(vActualLastOceanLegArrivalDTLocDT, "yyyy-MM-dd'T'HH:mm:ss", "HHmm")
                                            }
                                        } else if (vEstimateLastOceanLegArrivalDTLocDT != "") {
                                            outXml."S-DTM_19" {
                                                "E-374_19_01" "139"
                                                "E-373_19_02" util.convertDateTime(vEstimateLastOceanLegArrivalDTLocDT, "yyyy-MM-dd'T'HH:mm:ss", "yyyyMMdd")
                                                "E-337_19_03" util.convertDateTime(vEstimateLastOceanLegArrivalDTLocDT, "yyyy-MM-dd'T'HH:mm:ss", "HHmm")
                                            }
                                        }
                                    }
                                }
                        }
                    }
                }
        }
    }

    return writer.toString();
}

public String commonSubstring(String srcString, int startIndex, int substringLength) {
    if (startIndex > srcString.length()) {
        return ""
    } else {
        if (startIndex + substringLength >= srcString.length()) {
            return srcString.substring(startIndex)
        } else {
            return srcString.substring(startIndex, startIndex + substringLength)
        }
    }
}

public String ConcatphoneNumber(String CountryCode, String AreaCode, String Number) {
    String result;
    if (CountryCode != "" && AreaCode != "" && Number != "") {
        result = CountryCode.concat("-").concat(AreaCode).concat("-").concat(Number)
    } else if (CountryCode == "" && AreaCode != "" && Number != "") {
        result = AreaCode.concat("-").concat(Number);
    } else if (CountryCode == "" && AreaCode == "" && Number != "") {
        result = Number;
    }

    return result;
}