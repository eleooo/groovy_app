package cs.b2b.core.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import cs.b2b.core.common.classloader.HandlerClassesLoader;

public class GenericHandlerInvoker {

	static String ObsoleteKey = "*****[Obsolete Transaction]*****";
	
	@SuppressWarnings("rawtypes")
	public GenericHandlerInvokeResult call(String inputString, String sessionId, GenericHandlerClass cls, String[] runtimeParams, java.sql.Connection dbconnection) {
		//it can split out string as array for multiple output case.

		GenericHandlerInvokeResult ret = new GenericHandlerInvokeResult();
		if (cls==null || StringUtil.isEmpty(cls.className) || StringUtil.isEmpty(cls.methodName)) {
			ret.errorDescription = "Invalid Handler calling, "+(cls==null?"Null class instance, ":"ClassName: "+cls.className+", Method: "+cls.methodName);
			return ret;
		}
		
		try {
			Class clsInvoke = HandlerClassesLoader.getClassDef(cls.className+".class", cls.createTs, cls.classFileBase64);
			Class[] clsss = new Class[5];
			//inputString
			clsss[0] = String.class;
			//parameter setting in port config
			clsss[1] = String[].class;
			//session ID
			clsss[2] = String.class;
			//runtime parameter from designer, current only support 3 parameters
			clsss[3] = String[].class;
			clsss[4] = java.sql.Connection.class;
			
			@SuppressWarnings("unchecked")
			Method m = clsInvoke.getMethod(cls.methodName, clsss);
			
			Object[] obj = new Object[5];
			obj[0] = inputString;
			
			//1. set parameters handling value
			Object arrayObj = null;
			if (cls.paramStr!=null && cls.paramStr.trim().length()>0) {
				String[] strs = cls.paramStr.split("\\|\\*\\|");
				arrayObj = Array.newInstance(String.class, strs.length);
				for(int i=0; i<strs.length; i++) {
					Array.set(arrayObj, i, strs[i]);
				}
			}
			obj[1] = arrayObj;
	        //2. set session id
			obj[2] = sessionId;
	        //3. set runtime parameters
	        Object arrayObjRuntime = null;
	        if (runtimeParams!=null && runtimeParams.length>0) {
	        	arrayObjRuntime = Array.newInstance(String.class, runtimeParams.length);
	        	for(int io=0; io<runtimeParams.length; io++) {
	        		Array.set(arrayObjRuntime, io, runtimeParams[io]);
	        	}
	        }
	        obj[3] = arrayObjRuntime;
	        //set db connection if it has
	        obj[4] = dbconnection;
	        
	        //new instance everytime to avoid synchronized
			ret.returnStringArray = (String[])m.invoke(clsInvoke.newInstance(), obj);
			
		} catch (Error err) {
			StringBuffer sberror = new StringBuffer();
			StringWriter sw = new StringWriter();
			Throwable loge = err;
			for (int li=0; li<10; li++) {
				if (loge!=null && loge.getCause()!=null) {
					loge = loge.getCause();
				}
				if (loge.getCause()==null)
					break;
			}
			try {
				loge.printStackTrace(new PrintWriter(sw));
				sberror.append("Handler [").append(cls.className).append(", ver.").append(cls.version).append("] failed, ").append(sw.toString()).append(";; ");
				sw.close();
			} catch (Exception exx) {
				sberror.append("// Error // : ").append(err.toString());
			}
			ret.errorDescription = sberror.toString();
		} catch (Exception exp) {
			Throwable rootExp = exp.getCause();
			if (exp.getMessage()!=null && exp.getMessage().startsWith(ObsoleteKey)) {
				ret.obsoleteDescription = exp.getMessage().substring(ObsoleteKey.length());
			} else if (rootExp!=null && rootExp.getMessage()!=null && rootExp.getMessage().startsWith(ObsoleteKey)) {
				ret.obsoleteDescription = rootExp.getMessage().substring(ObsoleteKey.length());
			}
			
			if (ret.obsoleteDescription==null || ret.obsoleteDescription.equals("")) {
				StringBuffer sberror = new StringBuffer();
				StringWriter sw = new StringWriter();
				Throwable loge = exp;
				for (int li=0; li<10; li++) {
					if (loge!=null && loge.getCause()!=null) {
						loge = loge.getCause();
					}
					if (loge.getCause()==null)
						break;
				}
				try {
					loge.printStackTrace(new PrintWriter(sw));
					sberror.append("Handler [").append(cls.className).append(", ver.").append(cls.version).append("] failed, ").append(sw.toString()).append(";; ");
					sw.close();
				} catch (Exception exx) {
					sberror.append("// Error // : ").append(exp.toString());
				}
				ret.errorDescription = sberror.toString();
			}
		}
		return ret;
	}
	
	public static void main(String[] args) {
		String ErrorDescription = "";
		String OutputString[] = null;
		try {
			java.sql.Connection conn = null;
			//prepare handler class info
			cs.b2b.core.common.util.GenericHandlerClass hdlcls = new cs.b2b.core.common.util.GenericHandlerClass();
			hdlcls.className = "Receive_UnzipMsg"; 
			hdlcls.methodName = "receive"; 
			hdlcls.version = "2";
			hdlcls.paramStr = "";
			hdlcls.createTs = "2015-03-26T16:55:52+08:00";
			hdlcls.classFileBase64 = "yv66vgAAADEAiAcAAgEAIG9sbC9iMmIvaGFuZGxlci9SZWNlaXZlX1VuemlwTXNnBwAEAQAQamF2YS9sYW5nL09iamVjdAEABjxpbml0PgEAAygpVgEABENvZGUKAAMACQwABQAGAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEAIkxvbGwvYjJiL2hhbmRsZXIvUmVjZWl2ZV9VbnppcE1zZzsBABBnZXRMYXN0Q2hhbmdlZEJ5AQAUKClMamF2YS9sYW5nL1N0cmluZzsIABEBAAVEYXZpZAEAB3JlY2VpdmUBAHQoTGphdmEvbGFuZy9TdHJpbmc7W0xqYXZhL2xhbmcvU3RyaW5nO0xqYXZhL2xhbmcvU3RyaW5nO1tMamF2YS9sYW5nL1N0cmluZztMamF2YS9zcWwvQ29ubmVjdGlvbjspW0xqYXZhL2xhbmcvU3RyaW5nOwEACkV4Y2VwdGlvbnMHABYBABNqYXZhL2xhbmcvRXhjZXB0aW9uCgAYABoHABkBACBvbGwvYjJiL2NvcmUvY29tbW9uL3V0aWwvWmlwVXRpbAwAGwAcAQANZ2V0WmlwRmlsZU91dAEAKShMamF2YS9sYW5nL1N0cmluZzspTGphdmEvdXRpbC9IYXNodGFibGU7BwAeAQAXamF2YS9sYW5nL1N0cmluZ0J1aWxkZXIIACABABJVbnppcCBmYWlsdXJlLCBlOiAKAB0AIgwABQAjAQAVKExqYXZhL2xhbmcvU3RyaW5nOylWCgAVACUMACYADwEACmdldE1lc3NhZ2UKAB0AKAwAKQAqAQAGYXBwZW5kAQAtKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZ0J1aWxkZXI7CgAdACwMAC0ADwEACHRvU3RyaW5nCgAVACIKADAAMgcAMQEAE2phdmEvdXRpbC9IYXNodGFibGUMADMANAEABHNpemUBAAMoKUkIADYBABBFbXB0eSB6aXAgZW50cnkuCgAwADgMADkAOgEABGtleXMBABkoKUxqYXZhL3V0aWwvRW51bWVyYXRpb247CwA8AD4HAD0BABVqYXZhL3V0aWwvRW51bWVyYXRpb24MAD8AQAEAC25leHRFbGVtZW50AQAUKClMamF2YS9sYW5nL09iamVjdDsHAEIBABBqYXZhL2xhbmcvU3RyaW5nCgBBAEQMAEUADwEABHRyaW0KAEEARwwASAA0AQAGbGVuZ3RoCABKAQAaRW1wdHkgemlwIGVudHJ5IGZpbGUgbmFtZS4KADAATAwATQBOAQADZ2V0AQAmKExqYXZhL2xhbmcvT2JqZWN0OylMamF2YS9sYW5nL09iamVjdDsHAFABABZqYXZhL2xhbmcvU3RyaW5nQnVmZmVyCgBPACwBAAtpbnB1dFN0cmluZwEAEkxqYXZhL2xhbmcvU3RyaW5nOwEABnBhcmFtcwEAE1tMamF2YS9sYW5nL1N0cmluZzsBAAlzZXNzaW9uSWQBAA1ydW50aW1lUGFyYW1zAQAEY29ubgEAFUxqYXZhL3NxbC9Db25uZWN0aW9uOwEAC2hhc2haaXBGaWxlAQAVTGphdmEvdXRpbC9IYXNodGFibGU7AQABZQEAFUxqYXZhL2xhbmcvRXhjZXB0aW9uOwEAA2tleQEAAnNiAQAYTGphdmEvbGFuZy9TdHJpbmdCdWZmZXI7AQAWTG9jYWxWYXJpYWJsZVR5cGVUYWJsZQEAQUxqYXZhL3V0aWwvSGFzaHRhYmxlPExqYXZhL2xhbmcvU3RyaW5nO0xqYXZhL2xhbmcvU3RyaW5nQnVmZmVyOz47AQAEbWFpbgEAFihbTGphdmEvbGFuZy9TdHJpbmc7KVYIAGYBAFhEOlxvbGxiMmJfdGVzdGluZ1xyZWNvbmNpX3Jvb3RcUFJPRF9MTFBfU2FuTWFyX1BPU1RfUkVDVlx0ZXN0OTk5N19tc2didXNfcG9zdGFkdl8wMDEuemlwCgBoAGoHAGkBACZvbGwvYjJiL2NvcmUvY29tbW9uL3V0aWwvTG9jYWxGaWxlVXRpbAwAawBsAQAicmVhZEJpZ0ZpbGVDb250ZW50RGlyZWN0bHlUb0Jhc2U2NAEAJihMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9TdHJpbmc7CgABAAkIAG8BAAAKAAEAcQwAEgATCQBzAHUHAHQBABBqYXZhL2xhbmcvU3lzdGVtDAB2AHcBAANvdXQBABVMamF2YS9pby9QcmludFN0cmVhbTsIAHkBAANzOiAKAHsAfQcAfAEAE2phdmEvaW8vUHJpbnRTdHJlYW0MAH4AIwEAB3ByaW50bG4KABUAgAwAgQAGAQAPcHJpbnRTdGFja1RyYWNlAQAEYXJncwEADGZpbGVGdWxsTmFtZQEAAXMBAAJ1dQEAClNvdXJjZUZpbGUBABVSZWNlaXZlX1VuemlwTXNnLmphdmEAIQABAAMAAAAAAAQAAQAFAAYAAQAHAAAALwABAAEAAAAFKrcACLEAAAACAAoAAAAGAAEAAAAOAAsAAAAMAAEAAAAFAAwADQAAAAEADgAPAAEABwAAAC0AAQABAAAAAxIQsAAAAAIACgAAAAYAAQAAABEACwAAAAwAAQAAAAMADAANAAAAAQASABMAAgAUAAAABAABABUABwAAAUkABQAJAAAAgwE6Biu4ABc6BqcAIToHuwAVWbsAHVkSH7cAIRkHtgAktgAntgArtwAuvxkGxgALGQa2AC+aAA27ABVZEjW3AC6/GQa2ADe5ADsBAMAAQToHGQfGAA4ZB7YAQ7YARpoADbsAFVkSSbcALr8ZBhkHtgBLwABPOggEvQBBWQMZCLYAUVOwAAEAAwAJAAwAFQADAAoAAAAuAAsAAAAjAAMAJQAJACYADgAnACoAKQA3ACoAQQAsAFAALQBgAC4AagAwAHYAMgALAAAAZgAKAAAAgwAMAA0AAAAAAIMAUgBTAAEAAACDAFQAVQACAAAAgwBWAFMAAwAAAIMAVwBVAAQAAACDAFgAWQAFAAMAgABaAFsABgAOABwAXABdAAcAUAAzAF4AUwAHAHYADQBfAGAACABhAAAADAABAAMAgABaAGIABgAJAGMAZAABAAcAAACyAAYABAAAADwSZUwruABnTbsAAVm3AG1OLSwBEm4BAbYAcAMyTbIAcrsAHVkSeLcAISy2ACe2ACu2AHqnAAhMK7YAf7EAAQAAADMANgAVAAIACgAAACIACAAAADcAAwA4AAgAOQAQADoAHQA7ADMAPAA3AD0AOwA/AAsAAAA0AAUAAAA8AIIAVQAAAAMAMACDAFMAAQAIACsAhABTAAIAEAAjAIUADQADADcABABcAF0AAQABAIYAAAACAIc=";
			String InputString = "aabbcc";
			String SessionId = "";
			cs.b2b.core.common.util.GenericHandlerInvoker invoker = new cs.b2b.core.common.util.GenericHandlerInvoker();
			//call(String inputString, String sessionId, GenericHandlerClass cls, String[] runtimeParams, java.sql.Connection dbconnection)
			cs.b2b.core.common.util.GenericHandlerInvokeResult result = invoker.call(InputString, SessionId, hdlcls, null, conn);
			ErrorDescription = (result.errorDescription == null?"": result.errorDescription);
			OutputString = result.returnStringArray;
		} catch (Exception e) {
			ErrorDescription = e.toString();
		} finally {
			
		}
		System.out.println("->"+(OutputString==null?"":OutputString[0]));
		if (ErrorDescription.length()>0)
			System.out.println(ErrorDescription);
		
	}
	
}
