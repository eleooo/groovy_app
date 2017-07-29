package cs.b2b.core.common.util;

public class HandlerUtil {

	public static String getHandlerParameter(String name, String[] params) {
		for(int i=0; params!=null && i<params.length; i++) {
			String tmp = params[i];
			if (tmp==null || tmp.length()==0)
				continue;
			if (tmp.startsWith(name)) {
				if (tmp.startsWith(name+"==")) {
					return tmp.substring(tmp.indexOf("==")+2);
				}
			}
		}
		return "";
	}
}
