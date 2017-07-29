package cs.b2b.testing.reconci.tools.demo

/**
 * Created by LINJE2 on 7/26/2017.
 */
class Test {
    public static void main(String[] args) {
        def a="&apos;a?"
        println a
        if(a.indexOf("?")!=-1){
            print"成功"
            print a.substring(1,a.length()-1)
        }else{
            print "失败"
        }
    }
}
