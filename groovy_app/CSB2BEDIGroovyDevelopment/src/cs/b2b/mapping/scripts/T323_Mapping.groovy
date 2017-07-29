package cs.b2b.mapping.scripts

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

	
String mapping(String inputXmlBody, String[] runtimeParams, Connection conn) {
	
		cs.b2b.core.mapping.util.MappingUtil util = new cs.b2b.core.mapping.util.MappingUtil();
	
		/**
		 * Part I: prep handling here,
		 */
		inputXmlBody = util.removeBOM(inputXmlBody)
	
		/**
		 * Part II: get OLL mapping runtime parameters
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
		//def parser = new XmlParser()
		//parser.setNamespaceAware(false)
		//Important: the inputXml is xml root element
		def SSMXML = new XmlSlurper().parseText(inputXmlBody)
		//def iftmcs = new XmlSlurper().parseText(inputXmlBody);
	
		////update to CS2 XML root element, and use CTRL+SHIFT+O to auto import class
		//ContainerMovement containerMovement = JAXB.unmarshal(new StringReader(inputXmlBody), ContainerMovement.class)
		
		def writer = new StringWriter()
		def outXml = new MarkupBuilder(new IndentPrinter(new PrintWriter(writer), "", false));
		outXml.mkp.xmlDeclaration(version: "1.0", encoding: "utf-8")
	
		/**
		 * Part IV: mapping script start from here
		 */
	
		TimeZone.setDefault(TimeZone.getTimeZone('GMT+8'))
		def currentSystemDt = new Date()
		//please update foramt here
		def msgFmtId = "X.12"
		def ctrlNos = util.getEDIControlNumber("CARGOSMART", TP_ID, MSG_TYPE_ID, msgFmtId, conn)
		long ediIsaCtrlNumber = ctrlNos[0]
		long ediGroupCtrlNum = ctrlNos[1]
	
		outXml.'T323' {
			
			'ISA' {
				'ISA_01' '00'
				'ISA_02' '          '
				'ISA_03' '00'
				'ISA_04' '          '
				'ISA_05' 'ZZ'
				'ISA_06' 'SENDER'
				'ISA_07' 'ZZ'
				'ISA_08' 'GTNEXUS        '
				'ISA_09' currentSystemDt //yymmdd
				'ISA_10' currentSystemDt //hhmm
				'ISA_11' 'U'
				'ISA_12' '00401'
				'ISA_13' ediIsaCtrlNumber
				'ISA_14' '0'
				'ISA_15' 'T'
				'ISA_16' '>'
			}
			
			'GS' {
				'GS_01' 'SO'
				'GS_02' 'SENDER'
				'GS_03' 'GTNEXUS'
				'GS_04' currentSystemDt //yyyymmdd
				'GS_05' currentSystemDt //hhmm
				'GS_06' ediGroupCtrlNum
				'GS_07' 'X'
				'GS_08' '004010'
			}
			'ST_Loop' {
				'ST' {
					'ST_01' SSMXML.Message.test.text()
					'ST_02' 
				}
			}
			
			SSMXML.Message.RouteGroup.Route.eachWithIndex { currentRoute, RouteIdx ->
				'ST_Loop' {
					'ST' {
						'ST_01' '323'
						'ST_02' '-999'
					}
					'V1' {
						'V1_01' currentRoute.OceanComponent?.oceanleg[0]?.Vessel?.LloydsNumber.text()
						'V1_02' currentRoute.OceanComponent?.oceanleg[0]?.Vessel?.VesselName.text()
						'V1_03' ''
						
						if (currentRoute.OceanComponent?.oceanleg[0]?.Voyage?.ExternalVoyageNumber.text() !='') {
							'V1_04' currentRoute.OceanComponent?.oceanleg[0]?.Voyage.ExternalVoyageNumber.text()
						} else {
							'V1_04' currentRoute.OceanComponent?.oceanleg[0]?.Voyage?.InternalVoyageNumber.text() + currentRoute.OceanComponent?.oceanleg?.Direction.text()
							}
						'V1_05' currentRoute.Route?.Carrier?.SCAC.text()
						'V1_06' ''
						'V1_07' ''
						if (currentRoute.Route?.OceanComponent?.oceanleg[0]?.Vessel?.LloydsNumber.text() !='') {
						'V1_08' 'L'
						}
						'V1_09' 'O'
						
					}
					
					'K1' {
						if (4>currentRoute.OceanComponent?.oceanleg.size()) {
							def K1_01_UNLOCODE = ''
							def K1_01_ServiceCde = ''
							def K1_01_value=''
							
							currentRoute.OceanComponent?.oceanleg?.each { currentLeg ->
								K1_01_UNLOCODE = K1_01_UNLOCODE + currentLeg.POL?.UNLOCODE.text().substring(2, 2 + 3) + "-"
								K1_01_ServiceCde = K1_01_ServiceCde + "-" + currentLeg.service?.ServiceCode.text()
							}
							K1_01_value =K1_01_UNLOCODE + currentRoute.OceanComponent?.oceanleg?.last()?.POD?.UNLOCODE.text() + K1_01_ServiceCde
							
							'K1_01'  K1_01_value
							
						} else if (4==currentRoute.'pfx3:OceanComponent'?.'pfx3:oceanleg'.size()) {
							def K1_01_UNLOCODE = ''
							def K1_01_ServiceCde = ''
							def K1_01_value=''
							
							currentRoute.OceanComponent?.oceanleg?.each { currentLeg ->
								K1_01_UNLOCODE = K1_01_UNLOCODE + currentLeg.POL?.UNLOCODE.text().substring(2, 2 + 3) + "-"
								K1_01_ServiceCde = K1_01_ServiceCde + "-" + currentLeg.service?.ServiceCode.text()
							}
							K1_01_value =K1_01_UNLOCODE + currentRoute.OceanComponent?.oceanleg?.last()?.POD?.UNLOCODE.text() + "-"+ currentRoute.OceanComponent?.oceanleg[0]?.service?.ServiceCode.text() + "-TS-" +currentRoute.OceanComponent?.oceanleg?.last()?.service?.ServiceCode.text()
							
							'K1_01'  K1_01_value
						} else {
							def K1_01_UNLOCODE = ''
							def K1_01_ServiceCde = ''
							def K1_01_value=''
							
							currentRoute.OceanComponent?.oceanleg?.each { currentLeg ->
								K1_01_UNLOCODE = K1_01_UNLOCODE + currentLeg.POL?.UNLOCODE.text().substring(2, 2 + 3) + "-"
								K1_01_ServiceCde = K1_01_ServiceCde + "-" + currentLeg.service?.ServiceCode.text()
							}
							K1_01_value =K1_01_UNLOCODE + currentRoute.OceanComponent?.oceanleg?.last()?.POD?.UNLOCODE.text() + K1_01_ServiceCde
							
							'K1_01'  K1_01_value
						}
					}
					'R4_Loop' {
						'R4'{
							'R4_01' 'L'
							'R4_02' 'UN'
							
							def PORUNLocde = currentRoute.Origin?.City?.UNLOCODE.text()
							def PORFaciltyCde = currentRoute.OceanComponent?.oceanleg[0].POL?.FacilityCode.text()
							
							if (getEDICdeReffromIntCde(TP_ID,'UNLOCODECONV' , DIR_ID, 'SSXML', 'R4', PORUNLocde, PORFaciltyCde, conn)) {
								'R4_03' getEDICdeReffromIntCde(TP_ID,'UNLOCODECONV' , DIR_ID, 'SSXML', 'R4', PORUNLocde, PORFaciltyCde, conn)//code conversion
							} else {
								'R4_03' PORUNLocde
							}
							'R4_04' currentRoute.Origin?.City?.City.text().take(24)
							'R4_05' PORUNLocde.text().take(2)
						}
						currentRoute.OceanComponent?.oceanleg[0]?.POL?.Event.each { currentEvent ->
							if('Default_Cutoff' == currentEvent.Name.text()) {
								//Defaut_Cutoff
								'V9'{
									'V9_01' 'CRD'
									'V9_03' currentEvent.LocalDateTime.text()//yyyyMMdd
									'V9_04' currentEvent.LocalDateTime.text()//HHmm
								}
							} else if ('Estimate Berth Departure' == currentEvent.Name.text()) {
								//Estimate Berth Departure
								'V9'{
									'V9_01' 'EDD'
									'V9_03' currentEvent.LocalDateTime.text()//yyyyMMdd
									'V9_04' currentEvent.LocalDateTime.text()//HHmm
								}
							}
						}
					}
					
					//loop leg
					if (1<currentRoute.OceanComponent?.oceanleg.size()) {
						currentRoute.OceanComponent?.oceanleg.eachWithIndex { currentOceanLeg, curLegIdx ->
							if(currentRoute.OceanComponent?.oceanleg.size()>curLegIdx) {
								'R4_Loop' {
									'R4' {
										'R4_01' 'I'
										'R4_02' 'UN'
										
										def PODUNLocde = currentOceanLeg.POD?.UNLOCODE.text()
										def PODFaciltyCde = currentOceanLeg.POD?.FacilityCode.text()
										
										if (getEDICdeReffromIntCde(TP_ID,'UNLOCODECONV' , DIR_ID, 'SSXML', 'R4', PODUNLocde, PODFaciltyCde, conn)) {
											'R4_03' getEDICdeReffromIntCde(TP_ID,'UNLOCODECONV' , DIR_ID, 'SSXML', 'R4', PODUNLocde, PODFaciltyCde, conn) //code conversion
										} else {
											'R4_03' currentOceanLeg.POD?.UNLOCODE.text()
										}
										'R4_04' currentOceanLeg.POD?.PortName.text().take(24) 
										'R4_05' PODUNLocde.text().take(2)
									}
								
									// not complete
									if('Estimate Berth Arrival' == currentOceanLeg.POD?.Event?.Name?.text()) {
										//Defaut_Cutoff
										'V9'{
											'V9_01' 'EAD'
											'V9_03' currentEvent.LocalDateTime.text()//yyyyMMdd
											'V9_04' currentEvent.LocalDateTime.text()//HHmm
										}
									} 
									
									if ('Estimate Berth Departure' == currentRoute.currentEvent.Name.text()) {
										//Estimate Berth Departure
										'V9'{
											'V9_01' 'EDD'
											'V9_03' currentEvent.LocalDateTime.text()//yyyyMMdd
											'V9_04' currentEvent.LocalDateTime.text()//HHmm
										}
									}
								
								}
							}
						}
					}
					
					//FND
					'R4_Loop' {
						'R4' {
							'R4_01' 'D'
							'R4_02' 'UN'
							
							def FNDUNLocde = currentRoute.Destination?.City?.UNLOCODE.text()
							def PODLastFaciltyCde = currentRoute.OceanComponent?.oceanleg?.last()?.POD?.FacilityCode.text()
							
							if (getEDICdeReffromIntCde(TP_ID,'UNLOCODECONV' , DIR_ID, 'SSXML', 'R4', FNDUNLocde, PODLastFaciltyCde, conn)) {
								'R4_03' getEDICdeReffromIntCde(TP_ID,'UNLOCODECONV' , DIR_ID, 'SSXML', 'R4', FNDUNLocde, PODLastFaciltyCde, conn) //code conversion
							} else {
								'R4_03' FNDUNLocde.text()
							}
							'R4_04' currentRoute.Destination?.City?.City.text().take(24)
							'R4_05' FNDUNLocde.text().take(2)
						}
						
						currentRoute.OceanComponent?.oceanleg?.last()?.POD?.Event.each { currentEvent ->
							if('Estimate Berth Arrival' == currentEvent.Name.text()) {
								'V9'{
									'V9_01' 'EAD'
									'V9_03' currentEvent.LocalDateTime.text()//yyyyMMdd
									'V9_04' currentEvent.LocalDateTime.text()//HHmm
								}
							}
						}
					}
					
					'SE' {
						'SE_01' ''
						'SE_02' ediGroupCtrlNum
					}
				}
			}
			
			'GE' {
				'GE_01' ''
				'GE_02' ediGroupCtrlNum
			}
			'IEA' {
				'IEA_01' '1'
				'IEA_02' ediIsaCtrlNumber
			}
	
		}
		
		return writer.toString();
	}

