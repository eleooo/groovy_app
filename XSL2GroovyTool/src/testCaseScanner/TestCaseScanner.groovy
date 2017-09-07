package testCaseScanner

import b2b.LocalFileUtil;

class TestCaseScanner {

	static void main(String[] args){
		String outputPath = "C:\\SVN\\Regression\\Auto\\AllCT\\"
		File file = new File("C:\\SVN\\Regression\\Auto\\CT");
		file.listFiles().each{dir ->
			if(dir.isDirectory() && dir.name.startsWith("OUT-")){
				dir.listFiles().each {msgTypeDir ->
					if(msgTypeDir.isDirectory()){
						msgTypeDir.listFiles().each{tpDir ->
							if(tpDir.isFile()){
								String newFileName = tpDir.getAbsolutePath().replace(dir.getAbsolutePath()+"\\","").replace("\\","_");
								File output = new File(outputPath + newFileName);
								file.createNewFile();
								LocalFileUtil.writeToFile(outputPath + newFileName, tpDir.text,false); 
							}
						}
					}
				}
			}
		}
		
	}
	
}
