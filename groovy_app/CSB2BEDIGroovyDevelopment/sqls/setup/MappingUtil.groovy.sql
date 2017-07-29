SET SCAN OFF

-- MD5: 5f4da797d2c5d7881c20194e1d2f3e08
-- Groovy Script: MappingUtil.groovy
-- Reference from Script: Copy from MappingUtil.groovy


SELECT * FROM B2B_EDI_STYLESHEETS_REF WHERE NAME = 'MappingUtil.groovy' ORDER BY VERSION DESC;

DECLARE 

ScriptVersion number;
NewScriptVersion number;

var0 clob :='cGFja2FnZSBjcy5iMmIuY29yZS5tYXBwaW5nLnV0aWwKCmltcG9ydCBqYXZhLnNxbC5Db25uZWN0aW9uCmltcG9ydCBqYXZhLnNx';
var1 clob :='bC5QcmVwYXJlZFN0YXRlbWVudAppbXBvcnQgamF2YS5zcWwuUmVzdWx0U2V0CmltcG9ydCBqYXZhLnRleHQuU2ltcGxlRGF0ZUZv';
var2 clob :='cm1hdAoKCglwdWJsaWMgU3RyaW5nIGZvcm1hdFN0cmluZyhsb25nIHZhbCwgU3RyaW5nIG91dHB1dEZvcm1hdCkgewoJCVN0cmlu';
var3 clob :='ZyByZXQgPSB2YWw7CgkJaWYgKG91dHB1dEZvcm1hdCE9bnVsbCAmJiBvdXRwdXRGb3JtYXQubGVuZ3RoKCk+MCkgewoJCQlyZXQg';
var4 clob :='PSBTdHJpbmcuZm9ybWF0KG91dHB1dEZvcm1hdCwgdmFsKTsKCQl9CgkJcmV0dXJuIHJldDsKCX0KCglwdWJsaWMgYm9vbGVhbiBu';
var5 clob :='b3RFbXB0eShPYmplY3QgZGF0YSkgewoJCWlmIChkYXRhPT1udWxsKQoJCQlyZXR1cm4gZmFsc2U7CgkJCQoJCWlmIChkYXRhIGlu';
var6 clob :='c3RhbmNlb2YgU3RyaW5nKXsKCQkJcmV0dXJuIGRhdGEubGVuZ3RoKCkgPiAwCgkJfSBlbHNlewoJCQlyZXR1cm4gZGF0YSE9bnVs';
var7 clob :='bAoJCX0KCX0KCQoJcHVibGljIFN0cmluZyByZW1vdmVCT00oU3RyaW5nIHN0cikgewoJCWJ5dGVbXSBicyA9IHN0ci5nZXRCeXRl';
var8 clob :='cygpOwoJCVN0cmluZyBpbnN0ciA9IHN0cjsKCQlpZiAoLTE3ID09IGJzWzBdICYmIC02OSA9PSBic1sxXSAmJiAtNjUgPT0gYnNb';
var9 clob :='Ml0pIHsKCQkJaW5zdHIgPSBuZXcgU3RyaW5nKGJzLCAzLCBicy5sZW5ndGgtMyk7CgkJfSBlbHNlIGlmICgtMTcgPT0gYnNbMF0g';
var10 clob :='JiYgLTY1ID09IGJzWzFdICYmIC02NyA9PSBic1syXSkgewoJCQlpbnN0ciA9IG5ldyBTdHJpbmcoYnMsIDMsIGJzLmxlbmd0aC0z';
var11 clob :='KTsKCQl9IGVsc2UgaWYgKC0xNyA9PSBic1swXSAmJiAtNjkgPT0gYnNbMV0gJiYgNjMgPT0gYnNbMl0pIHsKCQkJaW5zdHIgPSBu';
var12 clob :='ZXcgU3RyaW5nKGJzLCAzLCBicy5sZW5ndGgtMyk7CgkJfSBlbHNlIGlmICg2MyA9PSBic1swXSAmJiA2MyA9PSBic1sxXSAmJiA2';
var13 clob :='MyA9PSBic1syXSkgewoJCQlpbnN0ciA9IG5ldyBTdHJpbmcoYnMsIDMsIGJzLmxlbmd0aC0zKTsKCQl9CgkJcmV0dXJuIGluc3Ry';
var14 clob :='OwoJfQoJCglwdWJsaWMgU3RyaW5nIGdldENvbnZlcnNpb25XaXRoRGVmYXVsdChTdHJpbmcgVFBfSUQsIFN0cmluZyBNU0dfVFlQ';
var15 clob :='RV9JRCwgU3RyaW5nIERJUl9JRCwgU3RyaW5nIGNvbnZlcnRUeXBlSWQsIFN0cmluZyBmcm9tVmFsdWUsIFN0cmluZyBkZWZhdWx0';
var16 clob :='VmFsdWUsIENvbm5lY3Rpb24gY29ubikgdGhyb3dzIEV4Y2VwdGlvbiB7CgkJU3RyaW5nIHJldCA9IGdldENvbnZlcnNpb24oVFBf';
var17 clob :='SUQsIE1TR19UWVBFX0lELCBESVJfSUQsIGNvbnZlcnRUeXBlSWQsIGZyb21WYWx1ZSwgY29ubik7CgkJaWYgKHJldD09bnVsbCB8';
var18 clob :='fCByZXQubGVuZ3RoKCk9PTApIHsKCQkJcmV0ID0gZGVmYXVsdFZhbHVlOwoJCX0KCQlyZXR1cm4gcmV0OwoJfQoJCglwdWJsaWMg';
var19 clob :='U3RyaW5nIGdldENvbnZlcnNpb25CeUV4dENkZVdpdGhEZWZhdWx0KFN0cmluZyBUUF9JRCwgU3RyaW5nIE1TR19UWVBFX0lELCBT';
var20 clob :='dHJpbmcgRElSX0lELCBTdHJpbmcgY29udmVydFR5cGVJZCwgU3RyaW5nIGZyb21WYWx1ZSwgU3RyaW5nIGRlZmF1bHRWYWx1ZSwg';
var21 clob :='Q29ubmVjdGlvbiBjb25uKSB0aHJvd3MgRXhjZXB0aW9uIHsKCQlTdHJpbmcgcmV0ID0gZ2V0Q29udmVyc2lvbkJ5RXh0Q2RlKFRQ';
var22 clob :='X0lELCBNU0dfVFlQRV9JRCwgRElSX0lELCBjb252ZXJ0VHlwZUlkLCBmcm9tVmFsdWUsIGNvbm4pOwoJCWlmIChyZXQ9PW51bGwg';
var23 clob :='fHwgcmV0Lmxlbmd0aCgpPT0wKSB7CgkJCXJldCA9IGRlZmF1bHRWYWx1ZTsKCQl9CgkJcmV0dXJuIHJldDsKCX0KCQoKCXB1Ymxp';
var24 clob :='YyBTdHJpbmcgZ2V0Q29udmVyc2lvbihTdHJpbmcgVFBfSUQsIFN0cmluZyBNU0dfVFlQRV9JRCwgU3RyaW5nIERJUl9JRCwgU3Ry';
var25 clob :='aW5nIGNvbnZlcnRUeXBlSWQsIFN0cmluZyBmcm9tVmFsdWUsIENvbm5lY3Rpb24gY29ubikgdGhyb3dzIEV4Y2VwdGlvbiB7CgkJ';
var26 clob :='aWYgKGNvbm49PW51bGwpCgkJCXJldHVybiAiIjsKCgkJU3RyaW5nIHJldCA9ICIiOwoJCVByZXBhcmVkU3RhdGVtZW50IHByZSA9';
var27 clob :='IG51bGw7CgkJUmVzdWx0U2V0IHJlc3VsdCA9IG51bGw7CgkJU3RyaW5nIHNxbCA9ICJzZWxlY3QgZXh0X2NkZSBmcm9tIGIyYl9j';
var28 clob :='ZGVfY29udmVyc2lvbiB3aGVyZSB0cF9pZD0/IGFuZCBtc2dfdHlwZV9pZD0/IGFuZCBkaXJfaWQ9PyBhbmQgY29udmVydF90eXBl';
var29 clob :='X2lkPT8gYW5kIGludF9jZGU9PyI7CgkJCgkJdHJ5IHsKCQkJcHJlID0gY29ubi5wcmVwYXJlU3RhdGVtZW50KHNxbCk7CgkJCXBy';
var30 clob :='ZS5zZXRTdHJpbmcoMSwgVFBfSUQpOwoJCQlwcmUuc2V0U3RyaW5nKDIsIE1TR19UWVBFX0lEKTsKCQkJcHJlLnNldFN0cmluZygz';
var31 clob :='LCBESVJfSUQpOwoJCQlwcmUuc2V0U3RyaW5nKDQsIGNvbnZlcnRUeXBlSWQpOwoJCQlwcmUuc2V0U3RyaW5nKDUsIGZyb21WYWx1';
var32 clob :='ZSk7CgkJCXJlc3VsdCA9IHByZS5leGVjdXRlUXVlcnkoKTsKCgkJCWlmIChyZXN1bHQubmV4dCgpKSB7CgkJCQlyZXQgPSByZXN1';
var33 clob :='bHQuZ2V0U3RyaW5nKDEpOwoJCQl9CgkJfSBmaW5hbGx5IHsKCQkJaWYgKHJlc3VsdCE9bnVsbCkKCQkJCXJlc3VsdC5jbG9zZSgp';
var34 clob :='OwoJCQlpZiAocHJlIT1udWxsKQoJCQkJcHJlLmNsb3NlKCk7CgkJfQoJCXJldHVybiByZXQ7Cgl9CgoJcHVibGljIFN0cmluZyBn';
var35 clob :='ZXRDb252ZXJzaW9uQnlFeHRDZGUoU3RyaW5nIFRQX0lELCBTdHJpbmcgTVNHX1RZUEVfSUQsIFN0cmluZyBESVJfSUQsIFN0cmlu';
var36 clob :='ZyBjb252ZXJ0VHlwZUlkLCBTdHJpbmcgZnJvbVZhbHVlLCBDb25uZWN0aW9uIGNvbm4pIHRocm93cyBFeGNlcHRpb24gewoJCWlm';
var37 clob :='IChjb25uPT1udWxsKQoJCQlyZXR1cm4gIiI7CgoJCVN0cmluZyByZXQgPSAiIjsKCQlQcmVwYXJlZFN0YXRlbWVudCBwcmUgPSBu';
var38 clob :='dWxsOwoJCVJlc3VsdFNldCByZXN1bHQgPSBudWxsOwoJCVN0cmluZyBzcWwgPSAic2VsZWN0IGludF9jZGUgZnJvbSBiMmJfY2Rl';
var39 clob :='X2NvbnZlcnNpb24gd2hlcmUgdHBfaWQ9PyBhbmQgbXNnX3R5cGVfaWQ9PyBhbmQgZGlyX2lkPT8gYW5kIGNvbnZlcnRfdHlwZV9p';
var40 clob :='ZD0/IGFuZCBleHRfY2RlPT8iOwoJCQoJCXRyeSB7CgkJCXByZSA9IGNvbm4ucHJlcGFyZVN0YXRlbWVudChzcWwpOwoJCQlwcmUu';
var41 clob :='c2V0U3RyaW5nKDEsIFRQX0lEKTsKCQkJcHJlLnNldFN0cmluZygyLCBNU0dfVFlQRV9JRCk7CgkJCXByZS5zZXRTdHJpbmcoMywg';
var42 clob :='RElSX0lEKTsKCQkJcHJlLnNldFN0cmluZyg0LCBjb252ZXJ0VHlwZUlkKTsKCQkJcHJlLnNldFN0cmluZyg1LCBmcm9tVmFsdWUp';
var43 clob :='OwoJCQlyZXN1bHQgPSBwcmUuZXhlY3V0ZVF1ZXJ5KCk7CgoJCQlpZiAocmVzdWx0Lm5leHQoKSkgewoJCQkJcmV0ID0gcmVzdWx0';
var44 clob :='LmdldFN0cmluZygxKTsKCQkJfQoJCX0gZmluYWxseSB7CgkJCWlmIChyZXN1bHQhPW51bGwpCgkJCQlyZXN1bHQuY2xvc2UoKTsK';
var45 clob :='CQkJaWYgKHByZSE9bnVsbCkKCQkJCXByZS5jbG9zZSgpOwoJCX0KCQlyZXR1cm4gcmV0OwoJfQoJCQoJcHVibGljIFN0cmluZyBn';
var46 clob :='ZXRDb252ZXJzaW9uV2l0aFNjYWMoU3RyaW5nIFRQX0lELCBTdHJpbmcgTVNHX1RZUEVfSUQsIFN0cmluZyBESVJfSUQsIFN0cmlu';
var47 clob :='ZyBjb252ZXJ0VHlwZUlkLCBTdHJpbmcgZnJvbVZhbHVlLCBTdHJpbmcgU0NBQywgQ29ubmVjdGlvbiBjb25uKSB0aHJvd3MgRXhj';
var48 clob :='ZXB0aW9uIHsKCQlpZiAoY29ubj09bnVsbCkKCQkJcmV0dXJuICIiOwoKCQlTdHJpbmcgcmV0ID0gIiI7CgkJUHJlcGFyZWRTdGF0';
var49 clob :='ZW1lbnQgcHJlID0gbnVsbDsKCQlSZXN1bHRTZXQgcmVzdWx0ID0gbnVsbDsKCQlTdHJpbmcgc3FsID0gInNlbGVjdCBleHRfY2Rl';
var50 clob :='IGZyb20gYjJiX2NkZV9jb252ZXJzaW9uIHdoZXJlIHRwX2lkPT8gYW5kIG1zZ190eXBlX2lkPT8gYW5kIGRpcl9pZD0/IGFuZCBj';
var51 clob :='b252ZXJ0X3R5cGVfaWQ9PyBhbmQgaW50X2NkZT0/IGFuZCBzY2FjX2NkZT0/IjsKCQkKCQl0cnkgewoJCQlwcmUgPSBjb25uLnBy';
var52 clob :='ZXBhcmVTdGF0ZW1lbnQoc3FsKTsKCQkJcHJlLnNldFN0cmluZygxLCBUUF9JRCk7CgkJCXByZS5zZXRTdHJpbmcoMiwgTVNHX1RZ';
var53 clob :='UEVfSUQpOwoJCQlwcmUuc2V0U3RyaW5nKDMsIERJUl9JRCk7CgkJCXByZS5zZXRTdHJpbmcoNCwgY29udmVydFR5cGVJZCk7CgkJ';
var54 clob :='CXByZS5zZXRTdHJpbmcoNSwgZnJvbVZhbHVlKTsKCQkJcHJlLnNldFN0cmluZyg2LCBTQ0FDKTsKCQkJcmVzdWx0ID0gcHJlLmV4';
var55 clob :='ZWN1dGVRdWVyeSgpOwoKCQkJaWYgKHJlc3VsdC5uZXh0KCkpIHsKCQkJCXJldCA9IHJlc3VsdC5nZXRTdHJpbmcoMSk7CgkJCX0K';
var56 clob :='CQl9IGZpbmFsbHkgewoJCQlpZiAocmVzdWx0IT1udWxsKQoJCQkJcmVzdWx0LmNsb3NlKCk7CgkJCWlmIChwcmUhPW51bGwpCgkJ';
var57 clob :='CQlwcmUuY2xvc2UoKTsKCQl9CgkJcmV0dXJuIHJldDsKCX0KCglwdWJsaWMgU3RyaW5nIGdldENvbnZlcnNpb25XaXRoU2NhY0J5';
var58 clob :='RXh0Q2RlKFN0cmluZyBUUF9JRCwgU3RyaW5nIE1TR19UWVBFX0lELCBTdHJpbmcgRElSX0lELCBTdHJpbmcgY29udmVydFR5cGVJ';
var59 clob :='ZCwgU3RyaW5nIGZyb21WYWx1ZSwgU3RyaW5nIFNDQUMsIENvbm5lY3Rpb24gY29ubikgdGhyb3dzIEV4Y2VwdGlvbiB7CgkJaWYg';
var60 clob :='KGNvbm49PW51bGwpCgkJCXJldHVybiAiIjsKCgkJU3RyaW5nIHJldCA9ICIiOwoJCVByZXBhcmVkU3RhdGVtZW50IHByZSA9IG51';
var61 clob :='bGw7CgkJUmVzdWx0U2V0IHJlc3VsdCA9IG51bGw7CgkJU3RyaW5nIHNxbCA9ICJzZWxlY3QgaW50X2NkZSBmcm9tIGIyYl9jZGVf';
var62 clob :='Y29udmVyc2lvbiB3aGVyZSB0cF9pZD0/IGFuZCBtc2dfdHlwZV9pZD0/IGFuZCBkaXJfaWQ9PyBhbmQgY29udmVydF90eXBlX2lk';
var63 clob :='PT8gYW5kIGV4dF9jZGU9PyBhbmQgc2NhY19jZGU9PyI7CgkJCgkJdHJ5IHsKCQkJcHJlID0gY29ubi5wcmVwYXJlU3RhdGVtZW50';
var64 clob :='KHNxbCk7CgkJCXByZS5zZXRTdHJpbmcoMSwgVFBfSUQpOwoJCQlwcmUuc2V0U3RyaW5nKDIsIE1TR19UWVBFX0lEKTsKCQkJcHJl';
var65 clob :='LnNldFN0cmluZygzLCBESVJfSUQpOwoJCQlwcmUuc2V0U3RyaW5nKDQsIGNvbnZlcnRUeXBlSWQpOwoJCQlwcmUuc2V0U3RyaW5n';
var66 clob :='KDUsIGZyb21WYWx1ZSk7CgkJCXByZS5zZXRTdHJpbmcoNiwgU0NBQyk7CgkJCXJlc3VsdCA9IHByZS5leGVjdXRlUXVlcnkoKTsK';
var67 clob :='CgkJCWlmIChyZXN1bHQubmV4dCgpKSB7CgkJCQlyZXQgPSByZXN1bHQuZ2V0U3RyaW5nKDEpOwoJCQl9CgkJfSBmaW5hbGx5IHsK';
var68 clob :='CQkJaWYgKHJlc3VsdCE9bnVsbCkKCQkJCXJlc3VsdC5jbG9zZSgpOwoJCQlpZiAocHJlIT1udWxsKQoJCQkJcHJlLmNsb3NlKCk7';
var69 clob :='CgkJfQoJCXJldHVybiByZXQ7Cgl9CgkKCXB1YmxpYyBTdHJpbmcgZ2V0Q29udmVyc2lvbkJ5Q29udmVydFR5cGUoU3RyaW5nIGNv';
var70 clob :='bnZlcnRUeXBlSWQsIFN0cmluZyBmcm9tVmFsdWUsIENvbm5lY3Rpb24gY29ubikgdGhyb3dzIEV4Y2VwdGlvbiB7CgkJU3RyaW5n';
var71 clob :='IHJldCA9IG51bGw7CgkJUHJlcGFyZWRTdGF0ZW1lbnQgcHJlID0gbnVsbDsKCQlSZXN1bHRTZXQgcmVzdWx0ID0gbnVsbDsKCQkK';
var72 clob :='CQlTdHJpbmcgc3FsID0gInNlbGVjdCBleHRfY2RlIGZyb20gYjJiX2NkZV9jb252ZXJzaW9uIHdoZXJlIGNvbnZlcnRfdHlwZV9p';
var73 clob :='ZD0/IGFuZCBpbnRfY2RlPT8iOwoKCQl0cnkgewoJCQlwcmUgPSBjb25uLnByZXBhcmVTdGF0ZW1lbnQoc3FsKTsKCQkJcHJlLnNl';
var74 clob :='dFN0cmluZygxLCBjb252ZXJ0VHlwZUlkKTsKCQkJcHJlLnNldFN0cmluZygyLCBmcm9tVmFsdWUpOwoJCQlyZXN1bHQgPSBwcmUu';
var75 clob :='ZXhlY3V0ZVF1ZXJ5KCk7CgoJCQlpZiAocmVzdWx0Lm5leHQoKSkgewoJCQkJcmV0ID0gcmVzdWx0LmdldFN0cmluZygxKTsKCQkJ';
var76 clob :='fQoJCX0gZmluYWxseSB7CgkJCWlmIChyZXN1bHQhPW51bGwpCgkJCQlyZXN1bHQuY2xvc2UoKTsKCQkJaWYgKHByZSE9bnVsbCkK';
var77 clob :='CQkJCXByZS5jbG9zZSgpOwoJCX0KCQlyZXR1cm4gcmV0OwoJfQoJCglwdWJsaWMgU3RyaW5nIGdldENhcnJpZXJUcElkKFN0cmlu';
var78 clob :='ZyBTZW5kZXJUcElkLCBTdHJpbmcgTXNnVHlwZSwgU3RyaW5nIFNjYWMsIENvbm5lY3Rpb24gY29ubikgdGhyb3dzIEV4Y2VwdGlv';
var79 clob :='biB7CgkJU3RyaW5nIHJldCA9IG51bGw7CgkJUHJlcGFyZWRTdGF0ZW1lbnQgcHJlID0gbnVsbDsKCQlSZXN1bHRTZXQgcmVzdWx0';
var80 clob :='ID0gbnVsbDsKCQkKCQlTdHJpbmcgc3FsID0gInNlbGVjdCBjaGFubmVsX3RwX2lkIGZyb20gdHBfaW50ZWdyYXRpb25fYXNzbyB3';
var81 clob :='aGVyZSBzZW5kZXJfdHBfaWQ9PyBhbmQgbWVzc2FnZV90eXBlPT8gYW5kIHJlY2VpdmVyX3NjYWNfY29kZT0/IjsKCgkJdHJ5IHsK';
var82 clob :='CQkJcHJlID0gY29ubi5wcmVwYXJlU3RhdGVtZW50KHNxbCk7CgkJCXByZS5zZXRTdHJpbmcoMSwgU2VuZGVyVHBJZCk7CgkJCXBy';
var83 clob :='ZS5zZXRTdHJpbmcoMiwgTXNnVHlwZSk7CgkJCXByZS5zZXRTdHJpbmcoMywgU2NhYyk7CgkJCXJlc3VsdCA9IHByZS5leGVjdXRl';
var84 clob :='UXVlcnkoKTsKCgkJCWlmIChyZXN1bHQubmV4dCgpKSB7CgkJCQlyZXQgPSByZXN1bHQuZ2V0U3RyaW5nKDEpOwoJCQl9CgkJfSBm';
var85 clob :='aW5hbGx5IHsKCQkJaWYgKHJlc3VsdCE9bnVsbCkKCQkJCXJlc3VsdC5jbG9zZSgpOwoJCQlpZiAocHJlIT1udWxsKQoJCQkJcHJl';
var86 clob :='LmNsb3NlKCk7CgkJfQoJCXJldHVybiByZXQ7Cgl9CgkKCXB1YmxpYyBTdHJpbmcgY29udmVydERhdGVUaW1lKFN0cmluZyBpbnB1';
var87 clob :='dERhdGUsIFN0cmluZyBpbnB1dEZvcm1hdCwgU3RyaW5nIG91dHB1dEZvcm1hdCkgdGhyb3dzIEV4Y2VwdGlvbiB7CgkJU3RyaW5n';
var88 clob :='IG91dHB1dCA9ICIiOwoKCQlpZiAoaW5wdXREYXRlIT1udWxsICYmIGlucHV0RGF0ZS50cmltKCkubGVuZ3RoKCk+MCAmJiBpbnB1';
var89 clob :='dEZvcm1hdCE9bnVsbCAmJiBpbnB1dEZvcm1hdC50cmltKCkubGVuZ3RoKCk+MAoJCSYmIG91dHB1dEZvcm1hdCE9bnVsbCAmJiBv';
var90 clob :='dXRwdXRGb3JtYXQudHJpbSgpLmxlbmd0aCgpPjApIHsKCgkJCVNpbXBsZURhdGVGb3JtYXQgc2ZtdCA9IG5ldyBTaW1wbGVEYXRl';
var91 clob :='Rm9ybWF0KGlucHV0Rm9ybWF0KTsKCQkJamF2YS51dGlsLkRhdGUgZGF0ZSA9IHNmbXQucGFyc2UoaW5wdXREYXRlKTsKCgkJCVNp';
var92 clob :='bXBsZURhdGVGb3JtYXQgc291dGZtdCA9IG5ldyBTaW1wbGVEYXRlRm9ybWF0KG91dHB1dEZvcm1hdCk7CgkJCW91dHB1dCA9IHNv';
var93 clob :='dXRmbXQuZm9ybWF0KGRhdGUpOwoJCX0KCgkJcmV0dXJuIG91dHB1dDsKCX0KCQoJCglwdWJsaWMgbG9uZyBHZXRTZXF1ZW5jZU5l';
var94 clob :='eHRWYWxXaXRoRGVmYXVsdChTdHJpbmcgc2VxS2V5LCBsb25nIGRlZmF1bHRWYWwsIENvbm5lY3Rpb24gY29ubikgewoJCS8vaW1w';
var95 clob :='bGVtZW50IHlvdXIgc2VxdWVuY2UgbG9naWMgaGVyZQoJCS8vVE9ETwoJCXJldHVybiAtMTsKCX0KCQoJcHVibGljIFN0cmluZyBn';
var96 clob :='ZXRSdW50aW1lUGFyYW1ldGVyKFN0cmluZyBuYW1lLCBTdHJpbmdbXSBwYXJhbXMpIHsKCQlTdHJpbmcgcG4gPSBuYW1lKyI9IjsK';
var97 clob :='CQlmb3IoaW50IGk9MDsgcGFyYW1zIT1udWxsICYmIGk8cGFyYW1zLmxlbmd0aDsgaSsrKSB7CgkJCVN0cmluZyB0bXAgPSBwYXJh';
var98 clob :='bXNbaV07CgkJCWlmICh0bXA9PW51bGwgfHwgdG1wLmxlbmd0aCgpPT0wKQoJCQkJY29udGludWU7CgkJCWlmICh0bXAuc3RhcnRz';
var99 clob :='V2l0aChwbikpIHsKCQkJCXJldHVybiB0bXAuc3Vic3RyaW5nKHBuLmxlbmd0aCgpKTsKCQkJfQoJCX0KCQlyZXR1cm4gIiI7Cgl9';
var100 clob :='CgkKCXB1YmxpYyBib29sZWFuIFNldE1vbkVESUNvbnRyb2xObyhTdHJpbmcgU2VuZGVySWQsIFN0cmluZyBQYXJ0bmVySWQsIFN0';
var101 clob :='cmluZyBNc2dUeXBlSWQsIFN0cmluZyBNc2dGbXRJZCwgU3RyaW5nIEVkaUludENoZ05vLCBTdHJpbmcgRWRpR3JwQ3RsTm8sIFN0';
var102 clob :='cmluZyBFZGlUeG5DdGxObywgU3RyaW5nIE1lc3NhZ2VJZCwgU3RyaW5nIE1zZ1JlcUlkLCBDb25uZWN0aW9uIGNvbm4pIHRocm93';
var103 clob :='cyBFeGNlcHRpb24gewoJCWJvb2xlYW4gcmV0ID0gZmFsc2U7CQoJCQoJCVByZXBhcmVkU3RhdGVtZW50IENoZWNrTW9uUHJlID0g';
var104 clob :='bnVsbDsKCQlQcmVwYXJlZFN0YXRlbWVudCBDaGVja0V4aXN0UHJlID0gbnVsbDsJCQoJCVByZXBhcmVkU3RhdGVtZW50IFVwZGF0';
var105 clob :='ZU1vbiA9IG51bGw7CgkJUHJlcGFyZWRTdGF0ZW1lbnQgSW5zZXJ0TW9uID0gbnVsbDsKCQkKCQlSZXN1bHRTZXQgQ2hlY2tNb25S';
var106 clob :='ZXN1bHQgPSBudWxsOwoJCVJlc3VsdFNldCBDaGVja0V4aXN0UmVzdWx0ID0gbnVsbDsKCQlSZXN1bHRTZXQgVXBkYXRlTW9uUmVz';
var107 clob :='dWx0ID0gbnVsbDsKCQlSZXN1bHRTZXQgSW5zZXJ0TW9uUmVzdWx0ID0gbnVsbDsKCQkKCQlTdHJpbmcgQ2hlY2tNb25TcWwgPSAi';
var108 clob :='c2VsZWN0IDEgZnJvbSBiMmJfZWRpX2ZpbGVuYW1lIHdoZXJlIFBBUlRORVJfSUQ9PyBhbmQgTVNHX1RZUEU9PyBhbmQgQUNLX01P';
var109 clob :='Tl9GTEFHIElTIE5PVCBOVUxMIjsKCQlTdHJpbmcgQ2hlY2tFeGlzdFNxbCA9ICJzZWxlY3QgQ1MyX01TR19JRCBmcm9tIGIyYl9l';
var110 clob :='ZGlfYWNrbW9uX2xvZyB3aGVyZSBTRU5ERVJfSUQ9PyBhbmQgQ1MyX01TR19JRD0/IjsKCQlTdHJpbmcgVXBkYXRlTW9uU3FsID0g';
var111 clob :='InVwZGF0ZSBiMmJfZWRpX2Fja21vbl9sb2cgc2V0IElOX01TR19SRVFfSUQ9PywgSU5fTVNHX1RZUEVfSUQ9PywgSU5fTVNHX0ZN';
var112 clob :='VF9JRD0/LCBJTl9JTlRDSEdfQ1RMTk89PywgSU5fR1JPVVBfQ1RMTk89PywgSU5fVFhOX0NUTE5PPT8sIElOX01TR19DUkVBVEVf';
var113 clob :='VFM9U1lTX0VYVFJBQ1RfVVRDKGN1cnJlbnRfdGltZXN0YW1wKSB3aGVyZSBDUzJfTVNHX0lEPT8iOyAKCQlTdHJpbmcgSW5zZXJ0';
var114 clob :='TW9uU3FsID0gImluc2VydCBpbnRvIGIyYl9lZGlfYWNrbW9uX2xvZyAoU0VOREVSX0lELCBSRUNFSVZFUl9JRCwgQ1MyX01TR19J';
var115 clob :='RCwgSU5fTVNHX1JFUV9JRCwgSU5fTVNHX1RZUEVfSUQsIElOX01TR19GTVRfSUQsIElOX0lOVENIR19DVExOTywgSU5fR1JPVVBf';
var116 clob :='Q1RMTk8sIElOX1RYTl9DVExOTywgSU5fTVNHX0NSRUFURV9UUykgdmFsdWVzICggPywgPywgPywgPywgPywgPywgPywgPywgPywg';
var117 clob :='U1lTX0VYVFJBQ1RfVVRDKGN1cnJlbnRfdGltZXN0YW1wKSkiOwoJCQkJCgkJdHJ5IHsKCQkJCgkJCUNoZWNrTW9uUHJlID0gY29u';
var118 clob :='bi5wcmVwYXJlU3RhdGVtZW50KENoZWNrTW9uU3FsKTsKCQkJQ2hlY2tNb25QcmUuc2V0U3RyaW5nKDEsIFNlbmRlcklkKTsKCQkJ';
var119 clob :='Q2hlY2tNb25QcmUuc2V0U3RyaW5nKDIsIE1zZ1R5cGVJZCk7CQkJCgkJCUNoZWNrTW9uUmVzdWx0ID0gQ2hlY2tNb25QcmUuZXhl';
var120 clob :='Y3V0ZVF1ZXJ5KCk7CgkJCWlmIChDaGVja01vblJlc3VsdCE9bnVsbCkgewoJCQkJQ2hlY2tFeGlzdFByZSA9IGNvbm4ucHJlcGFy';
var121 clob :='ZVN0YXRlbWVudChDaGVja0V4aXN0U3FsKTsKCQkJCUNoZWNrRXhpc3RQcmUuc2V0U3RyaW5nKDEsIFNlbmRlcklkKTsKCQkJCUNo';
var122 clob :='ZWNrRXhpc3RQcmUuc2V0U3RyaW5nKDIsIE1lc3NhZ2VJZCk7CgkJCQlDaGVja0V4aXN0UmVzdWx0ID0gQ2hlY2tFeGlzdFByZS5l';
var123 clob :='eGVjdXRlUXVlcnkoKTsKCQkJCQoJCQkJaWYgKENoZWNrRXhpc3RSZXN1bHQucm93PjApIHsKCQkJCQlVcGRhdGVNb24gPSBjb25u';
var124 clob :='LnByZXBhcmVTdGF0ZW1lbnQoVXBkYXRlTW9uU3FsKTsKCQkJCQlVcGRhdGVNb24uc2V0U3RyaW5nKDEsIE1zZ1JlcUlkKTsKCQkJ';
var125 clob :='CQlVcGRhdGVNb24uc2V0U3RyaW5nKDIsIE1zZ1R5cGVJZCk7CgkJCQkJVXBkYXRlTW9uLnNldFN0cmluZygzLCBNc2dGbXRJZCk7';
var126 clob :='CgkJCQkJVXBkYXRlTW9uLnNldFN0cmluZyg0LCBFZGlJbnRDaGdObyk7CgkJCQkJVXBkYXRlTW9uLnNldFN0cmluZyg1LCBFZGlH';
var127 clob :='cnBDdGxObyk7CgkJCQkJVXBkYXRlTW9uLnNldFN0cmluZyg2LCBFZGlUeG5DdGxObyk7CgkJCQkJVXBkYXRlTW9uLnNldFN0cmlu';
var128 clob :='Zyg3LCBNZXNzYWdlSWQpOwkJCQoJCQkJCWlmIChVcGRhdGVNb24uZXhlY3V0ZVVwZGF0ZSgpPjApIHsKCQkJCQkJcmV0ID0gdHJ1';
var129 clob :='ZTsKCQkJCQl9IGVsc2UgewoJCQkJCQkKCQkJCQkJcmV0ID0gZmFsc2U7CgkJCQkJfQoJCQkJfSBlbHNlIHsKCQkJCQlJbnNlcnRN';
var130 clob :='b24gPSBjb25uLnByZXBhcmVTdGF0ZW1lbnQoSW5zZXJ0TW9uU3FsKTsKCQkJCQlJbnNlcnRNb24uc2V0U3RyaW5nKDEsIFNlbmRl';
var131 clob :='cklkKTsKCQkJCQlJbnNlcnRNb24uc2V0U3RyaW5nKDIsIFBhcnRuZXJJZCk7CgkJCQkJSW5zZXJ0TW9uLnNldFN0cmluZygzLCBN';
var132 clob :='ZXNzYWdlSWQpOwoJCQkJCUluc2VydE1vbi5zZXRTdHJpbmcoNCwgTXNnUmVxSWQpOwoJCQkJCUluc2VydE1vbi5zZXRTdHJpbmco';
var133 clob :='NSwgTXNnVHlwZUlkKTsKCQkJCQlJbnNlcnRNb24uc2V0U3RyaW5nKDYsIE1zZ0ZtdElkKTsKCQkJCQlJbnNlcnRNb24uc2V0U3Ry';
var134 clob :='aW5nKDcsIEVkaUludENoZ05vKTsKCQkJCQlJbnNlcnRNb24uc2V0U3RyaW5nKDgsIEVkaUdycEN0bE5vKTsKCQkJCQlJbnNlcnRN';
var135 clob :='b24uc2V0U3RyaW5nKDksIEVkaVR4bkN0bE5vKTsKCQkJCQlpZiAoSW5zZXJ0TW9uLmV4ZWN1dGVVcGRhdGUoKT4wKSB7CgkJCQkJ';
var136 clob :='CXJldCA9IHRydWU7CgkJCQkJfSBlbHNlIHsKCQkJCQkJcmV0ID0gZmFsc2U7CgkJCQkJfQoJCQkJfQkJCQkJCgkJCX0KCQl9IGZp';
var137 clob :='bmFsbHkgewoJCQlpZiAoQ2hlY2tNb25SZXN1bHQhPW51bGwpCgkJCQlDaGVja01vblJlc3VsdC5jbG9zZSgpOwoJCQlpZiAoQ2hl';
var138 clob :='Y2tFeGlzdFJlc3VsdCE9bnVsbCkKCQkJCUNoZWNrRXhpc3RSZXN1bHQuY2xvc2UoKTsKCQkJaWYgKFVwZGF0ZU1vblJlc3VsdCE9';
var139 clob :='bnVsbCkKCQkJCVVwZGF0ZU1vblJlc3VsdC5jbG9zZSgpOwoJCQlpZiAoSW5zZXJ0TW9uUmVzdWx0IT1udWxsKQoJCQkJSW5zZXJ0';
var140 clob :='TW9uUmVzdWx0LmNsb3NlKCk7CgkJfQkJCQkKCQlyZXR1cm4gcmV0OwoJfQoJCg==';


BEGIN
   
    SELECT NVL(MAX(VERSION),0) INTO ScriptVersion from B2B_EDI_STYLESHEETS_REF WHERE NAME = 'MappingUtil.groovy';    
    
    NewScriptVersion := ScriptVersion + 1;

    INSERT INTO B2B_EDI_STYLESHEETS_REF(NAME,VERSION,DESCRIPTION,CREATE_TS,CREATE_BY,STYLE_SHEET)
    VALUES('MappingUtil.groovy',NewScriptVersion, 'Copy from MappingUtil.groovy', SYSDATE,'AUTO_GENERATE',var0||var1||var2||var3||var4||var5||var6||var7||var8||var9||var10||var11||var12||var13||var14||var15||var16||var17||var18||var19||var20||var21||var22||var23||var24||var25||var26||var27||var28||var29||var30||var31||var32||var33||var34||var35||var36||var37||var38||var39||var40||var41||var42||var43||var44||var45||var46||var47||var48||var49||var50||var51||var52||var53||var54||var55||var56||var57||var58||var59||var60||var61||var62||var63||var64||var65||var66||var67||var68||var69||var70||var71||var72||var73||var74||var75||var76||var77||var78||var79||var80||var81||var82||var83||var84||var85||var86||var87||var88||var89||var90||var91||var92||var93||var94||var95||var96||var97||var98||var99||var100||var101||var102||var103||var104||var105||var106||var107||var108||var109||var110||var111||var112||var113||var114||var115||var116||var117||var118||var119||var120||var121||var122||var123||var124||var125||var126||var127||var128||var129||var130||var131||var132||var133||var134||var135||var136||var137||var138||var139||var140);

    INSERT INTO B2B_EDI_STYLESHEETS_REF_LOG
    SELECT * FROM B2B_EDI_STYLESHEETS_REF WHERE NAME='MappingUtil.groovy' AND VERSION=ScriptVersion;

    DELETE B2B_EDI_STYLESHEETS_REF WHERE NAME='MappingUtil.groovy' AND VERSION=ScriptVersion;

END;
/

COMMIT;

SELECT * FROM B2B_EDI_STYLESHEETS_REF WHERE NAME = 'MappingUtil.groovy' ORDER BY VERSION DESC;

