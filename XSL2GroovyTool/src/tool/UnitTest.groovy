package tool

import org.junit.Assert

/**
 * Created by LINJE2 on 7/29/2017.
 */
class UnitTest {


    CompareJson com=new CompareJson()
    @org.junit.Test
    public void test(){
        String str1="{\"age\":43, \"friend_ids\":[16, 52, 23]}"
        String str2="{\'friend_ids\':[52, 23, 16]}"
        Assert.assertTrue(com?.compare(str1,str2)==false)
        println "=========case1=========="


        str1="{\"age\":43, \"friend_ids\":[16, 52, 23]}"
        str2="{\"age\":43, \"friend_ids\":[16, 52, 23]}"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case2=========="
        str1="{\"age\":43, \"friend_ids\":[16, 52, 23],\"aa\":{\"bb\":\"bb\",\"cc\":\"\"}}"
        str2="{\"age\":43, \"friend_ids\":[16, 52, 23],\"aa\":{\"bb\":\"bb\"}}"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case3=========="

        str1="{\"age\":43, \"friend_\":\"\", \"friend_ids\":[16, 52, 23]}"
        str2="{\"age\":43, \"friend_ids\":[16, 52, 23]}"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case4=========="

        str1="{\"age\":43, \"friend_\":'', \"friend_ids\":[16, 52, 23]}"
        str2="{\"age\":43, \"friend_ids\":[16, 52, 23]}"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case5=========="

        str1="{\"age\":43, \"friend_\":\"\", \"friend_ids\":[16, 52, 23]}"
        str2="{\"age\":43, \"friend_ids\":[16, 52, 2]}"
        Assert.assertTrue(com.compare(str1,str2)==false)
        println "=========case6=========="

        str1="{\"age\":43, \"friend_\":\"\", \"friend_ids\":[16, 52, 23]}"
        str2="[\"ZHULI\",\"30\",\"ALI\"]"
        Assert.assertTrue(com.compare(str1,str2)==false)
        println "=========case7=========="

        str1="[\"ZHULI\",\"30\",\"ALI\"]"
        str2="[\"ZHULI\",\"30\",\"ALI\"]"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case8=========="

        str1="[\"ZHULI\",\"30\",\"ALI\",[]]"
        str2="[\"ZHULI\",\"30\",\"ALI\"]"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case9=========="

        str1="[\"ZHULI\",\"30\",\"ALI\"]"
        str2="[\"ZHULI\",\"30\",\"ALI\",{}]"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case10=========="

        str1="[\"ZHULI\",\"ALI\"]"
        str2="[\"ZHULI\",\"30\",\"ALI\"]"
        Assert.assertTrue(com.compare(str1,str2)==false)
        println "=========case11=========="

        str1="[\"ZHULI\",\"\",\"ALI\"]"
        str2="[\"ZHULI\",\"\",\"ALI\"]"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case12=========="

        str1="[\"ZHULI\",\"ALI\"]"
        str2="[\"ZHULI\",\"null\",\"ALI\"]"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case13=========="

        str1="[\"ZHULI\",\"ALI\"]"
        str2="[\"ZHULI\",null,\"ALI\"]"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case14=========="

        str1="[\"ZHULI\",\"ALI\",[\"ZHULI\",\"ALI\"],{'MessageBody':{'MessageProperties':{'HaulageDetails':{'attr_MovementType':'PortToPort','attr_ServiceType':'FullLoad'}}}}]"
        str2="[\"ZHULI\",\"null\",\"ALI\",[\"ZHULI\",\"null\",\"ALI\"],{\"MessageBody\":{\"MessageProperties\":{\"ExportLicenseDetails\":{},\"ReferenceInformation\":[],\"HaulageDetails\":{\"attr_MovementType\":\"PortToPort\",\"attr_ServiceType\":\"FullLoad\"}}}}]"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case15=========="

        str1="[\"ZHULI\",\"ALI\",[\"ZHULI\",\"ALI\"]]"
        str2="[\"ZHULI\",\"null\",\"ALI\",[\"ZHULI\",\"\",\"ALI\"]]"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case16=========="

        str1="{'MessageBody':{'MessageProperties':{'HaulageDetails':{'attr_MovementType':'PortToPort','attr_ServiceType':'FullLoad'}}}}"
        str2="{\"MessageBody\":{\"MessageProperties\":{\"ExportLicenseDetails\":[],\"ReferenceInformation\":[],\"HaulageDetails\":{\"attr_MovementType\":\"PortToPort\",\"attr_ServiceType\":\"FullLoad\"}}}}"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case17=========="

        str1="{'MessageBody':{'MessageProperties':{'HaulageDetails':{'attr_MovementType':'PortToPort','attr_ServiceType':'FullLoad'}}}}"
        str2="{\"MessageBody\":{\"MessageProperties\":{\"ExportLicenseDetails\":{},\"ReferenceInformation\":[],\"HaulageDetails\":{\"attr_MovementType\":\"PortToPort\",\"attr_ServiceType\":\"FullLoad\"}}}}"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case18=========="

        str1="{'MessageBody':{'MessageProperties':{'HaulageDetails':{'attr_MovementType':'PortToPort','attr_ServiceType':'FullLoad'}}}}"
        str2="{\"MessageBody\":{\"MessageProperties\":{\"ExportLicenseDetails\":{},\"ReferenceInformation\":null,\"HaulageDetails\":{\"attr_MovementType\":\"PortToPort\",\"attr_ServiceType\":\"FullLoad\"}}}}"
        Assert.assertTrue(com.compare(str1,str2)==true)
        println "=========case19=========="
    }

}
