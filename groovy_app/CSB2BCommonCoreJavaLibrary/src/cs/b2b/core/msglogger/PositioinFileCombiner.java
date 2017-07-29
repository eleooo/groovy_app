package cs.b2b.core.msglogger;

public class PositioinFileCombiner {

	// / <summary>
	// / Combine positional header and detail file to one output file
	// / 1 header line + multiple detail line (multiple = 0 : unbounded)
	// / </summary>
	// / <param name="fileFullNameHeader">header file full name</param>
	// / <param name="headerKeyStartPosition">key's position, start from
	// 1</param>
	// / <param name="keyLength">key's length</param>
	// / <param name="fileFullNameDetail">detail file full name</param>
	// / <param name="detailKeyStartPosition">key's position, start from
	// 1</param>
	// / <param name="headerAddTag">add Tag when write header line to output
	// file</param>
	// / <param name="detailAddTag">add Tag when write detail line to outupt
	// file</param>
	// / <returns>String - combine output string</returns>
	public String combinePositionFileByKey(String headerFileContent,
			int headerKeyStartPosition, int keyLength,
			String detailFileContent, int detailKeyStartPosition,
			String headerAddTag, String detailAddTag) throws Exception {
		String recordDelimiter = "\r\n";
		if (!detailFileContent.contains(recordDelimiter)) {
			if (detailFileContent.contains("\n"))
				recordDelimiter = "\n";
			else
				throw new Exception("Can't find line delimiter");
		}

		String[] detailList = detailFileContent.split(recordDelimiter);
		String[] headerList = headerFileContent.split(recordDelimiter);
		StringBuffer outsb = new StringBuffer();

		for (String strHeaderLine : headerList) {
			if (strHeaderLine == null || strHeaderLine.trim().length() == 0)
				continue;
			// get key for header file
			if (strHeaderLine.length() < (headerKeyStartPosition - 1 + keyLength))
				continue;
			String headerKey = strHeaderLine.substring(
					headerKeyStartPosition - 1, headerKeyStartPosition - 1
							+ keyLength);

			outsb.append(headerAddTag).append(strHeaderLine)
					.append(recordDelimiter);

			for (String _str : detailList) {
				if (_str.length() < (detailKeyStartPosition - 1 + keyLength))
					continue;
				String _detailKey = _str.substring(detailKeyStartPosition - 1,
						detailKeyStartPosition - 1 + keyLength);
				if (_detailKey.equals(headerKey)) {
					outsb.append(detailAddTag).append(_str)
							.append(recordDelimiter);
				}
			}
		}
		return outsb.toString();
	}
}
