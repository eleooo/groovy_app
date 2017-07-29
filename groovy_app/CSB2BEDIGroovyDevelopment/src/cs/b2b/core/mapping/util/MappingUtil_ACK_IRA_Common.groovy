package cs.b2b.core.mapping.util

import cs.b2b.core.mapping.bean.ack.fa.FunctionalAcknowlegment
import groovy.xml.MarkupBuilder
import java.sql.Connection

/**
 * Created by HUANGSU3 on 7/5/2017.
 */
class MappingUtil_ACK_IRA_Common {

    private cs.b2b.core.mapping.util.MappingUtil util

    public MappingUtil_ACK_IRA_Common() {
    }

    public MappingUtil_ACK_IRA_Common(cs.b2b.core.mapping.util.MappingUtil util) {
        this.util = util;
    }

    public void buildBizKey(MarkupBuilder bizKeyXml, FunctionalAcknowlegment fa,int current_BodyIndex, def errorKeyList, String TP_ID, Connection conn) {
        def bizKey = []
        // L1P for FA
        //remarks sample :  Original EDI MSGID: xxxxxxx || xxxxxxxxxxxxxxxxxxx-1 || xxxxxxxxxxxxxxxxxxx-2 ||
        def PRE_MSG_REQ_ID = null
        if (util.notEmpty(fa?.Information?.Remark)){
            String str = fa?.Information?.Remark
            def remakrsList=str.split("\\|\\|")
            remakrsList?.each { current_remarksItem ->
                println current_remarksItem.toString()
                if(current_remarksItem.contains("Original EDI MSGID:")) {
                    PRE_MSG_REQ_ID = current_remarksItem.substring(current_remarksItem.indexOf(":") + 1, current_remarksItem.length()).trim()
                }
            }
        }

        if(util.isEmpty(PRE_MSG_REQ_ID))
             return

        String montype = util.getMonTypeFromE2EMon(TP_ID,PRE_MSG_REQ_ID,conn)
        HashMap<String, String> bizkeys = []

        if(util.isEmpty(montype))
            return

        bizkeys.put(montype, PRE_MSG_REQ_ID)
        bizKey.add(bizkeys)

        // L1RTPID
        bizKey.add(["L1RTPID" : TP_ID])
        //L1STPID
        bizKey.add(["L1STPID" : TP_ID])

        bizKeyXml.'ns0:Transaction'('xmlns:ns0':'http://www.tibco.com/schemas/message-processing/Schemas/System/BizKeyTrack.xsd') {
            //filter duplicate items
            def addItems = []
            bizKey.each { currentBizKeyMap ->
                currentBizKeyMap.each { key, value ->
                    String val = util.substring(value, 1, 100)
                    String _duplicateVal = key + ", value: " + val
                    if (addItems.contains(_duplicateVal) || val == null || val.trim() == '') {
                        //loop next item
                        return
                    } else {
                        addItems.add(_duplicateVal)
                    }
                    'ns0:BizKey' {
                        'ns0:Type' key
                        'ns0:Value' val
                    }
                }
            }
            addItems.clear()
        }
    }

    public void promoteBizKeyToSession(String appSessionId, StringWriter bizKeyWriter) {
        String bizkey = bizKeyWriter?.toString()
        cs.b2b.core.common.session.B2BRuntimeSession.addSessionValue(appSessionId, 'PROMOTE_SESSION_BIZKEY', bizkey)
    }

}
