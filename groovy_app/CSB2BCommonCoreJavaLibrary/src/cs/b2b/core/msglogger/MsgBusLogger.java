package cs.b2b.core.msglogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MsgBusLogger {

	static SimpleDateFormat ollSeqIdTsFmt = new SimpleDateFormat(
			"yyMMdd_HHmmss_SSS");

	// / <summary>
	// /
	// / If the sequence exist, then increment the Sequence Value by 1, and then
	// return the Sequence Value
	// / If the sequence is not exist, will create a permanent sequence with
	// default value, and then return default value
	// /
	// / </summary>
	// / <param name="sSeqID"></param>
	// / <param name="nDefaultVal"></param>
	// / <returns></returns>
	public long GetSequenceNextValWithDefault(Connection conn, String sSeqID,
			long nDefaultVal) throws Exception {
		boolean needCheckValueMinMax = false;
		return GetSequenceNextValWithDefaultMaximumImpl(conn, sSeqID,
				nDefaultVal, -1, needCheckValueMinMax, 0);
	}

	public long GetSequenceNextValWithDefaultAndMaximum(Connection conn,
			String sSeqID, long nDefaultVal, long nMaximumVal) throws Exception {
		boolean needCheckValueMinMax = true;
		return GetSequenceNextValWithDefaultMaximumImpl(conn, sSeqID,
				nDefaultVal, nMaximumVal, needCheckValueMinMax, 0);
	}

	// public long GetSequenceNextValWithTempAndDefaultMaximum(Connection conn,
	// String sSeqID, boolean isTemp, long nDefaultVal, long nMaximumVal) throws
	// Exception {
	// return GetSequenceNextValWithTempAndDefaultMaximumStepVal(conn, sSeqID,
	// isTemp, nDefaultVal, nMaximumVal, 1);
	// }

	public long GetSequenceNextValWithDefaultIncreaseStepVal(Connection conn,
			String sSeqID, long nDefaultVal, long increaseStepVal)
			throws Exception {
		boolean needCheckValueMinMax = false;
		return GetSequenceNextValWithDefaultMaximumImpl(conn, sSeqID,
				nDefaultVal, -1, needCheckValueMinMax, increaseStepVal);
	}

	// / <summary>
	// / If the sequence exist, then increment the Sequence Value by 1, and then
	// return the Sequence Value
	// / If the sequence is not exist, will create the sequence with default
	// value, and then return default value
	// /
	// / isTemp - whether temp sequence of creating seq
	// / nMaximumVal - maximum sequence value, if value equals to nMaximumVal,
	// will set to nDefaultVal
	// / nMaximumVal > 0 indicate this parameter is active, -1 indicate not need
	// maximum limitation
	// / </summary>
	// / <param name="sSeqID"></param>
	// / <param name="nDefaultVal"></param>
	// / <param name="isTemp"></param>
	// / <returns></returns>
	private synchronized long GetSequenceNextValWithDefaultMaximumImpl(
			Connection conn, String sSeqID, long nDefaultVal, long nMaximumVal,
			boolean needCheckValueMinMax, long increaseStepVal)
			throws Exception {
		if (sSeqID == null)
			return -1;

		long retValue = -1;
		long currentValue = -1;
		// for existing checking

		String sqlCheck = "SELECT seq_id, oracle_sequence_id, seq_val, update_ts, val_minimum, val_maximum, val_increment_by FROM oll_seq WHERE seq_id = ?";
		String sqlUpdate = "";
		ResultSet rs = null;
		PreparedStatement pst = null;
		String settingOracleSeqId = "";
		long dbsettingMin = -1;
		long dbsettingMax = -1;
		int dbsettingIncrementBy = 0;

		try {
			if (conn == null)
				throw new Exception("DB Connection is null.");
			if (conn.isClosed())
				throw new Exception("DB Connection is closed.");

			// sqlTrans = conn.BeginTransaction(IsolationLevel.Serializable);

			// check whether the sSeqId is exist
			pst = conn.prepareStatement(sqlCheck);
			pst.setString(1, sSeqID);
			rs = pst.executeQuery();
			boolean isExists = false;
			if (rs != null && rs.next()) {
				isExists = true;
				currentValue = rs.getLong("seq_val");
				settingOracleSeqId = rs.getString("oracle_sequence_id");
				settingOracleSeqId = (settingOracleSeqId == null ? ""
						: settingOracleSeqId.trim());
				dbsettingMin = rs.getLong("val_minimum");
				dbsettingMax = rs.getLong("val_maximum");
				dbsettingIncrementBy = rs.getInt("val_increment_by");
			}

			close(pst, rs);

			if (isExists) {
				if (settingOracleSeqId.length() > 0) {
					pst = conn.prepareStatement("select " + settingOracleSeqId
							+ ".Nextval from dual");
					rs = pst.executeQuery();
					if (rs != null && rs.next()) {
						retValue = rs.getLong(1);
					} else {
						throw new Exception("Can't get sequence for [" + sSeqID
								+ "], oracle sequence id: "
								+ settingOracleSeqId + ", pls check db.");
					}
					if (needCheckValueMinMax) {
						long maxVal = (nMaximumVal >= 0 ? nMaximumVal
								: 9999999999999L);
						long minVal = nDefaultVal;
						int increaseStep = 1;
						if (minVal > maxVal) {
							minVal = maxVal;
							maxVal = nDefaultVal;
							increaseStep = -1;
						}
						if (dbsettingMin != minVal || dbsettingMax != maxVal
								|| dbsettingIncrementBy != increaseStep) {
							close(pst, rs);

							if (retValue < minVal || retValue > maxVal) {
								throw new Exception(
										"Cannot reset sequence as current value ("
												+ retValue
												+ ") is not in scope between changing min and max ("
												+ minVal + " - " + maxVal
												+ ").");
							}

							String sqlUpdateSeqForSetting = "update oll_seq set val_minimum=?, val_maximum=?, val_start_with=?, val_increment_by=?, update_ts=sysdate where seq_id=?";
							pst = conn.prepareStatement(sqlUpdateSeqForSetting);
							pst.setLong(1, minVal);
							pst.setLong(2, maxVal);
							pst.setLong(3, nDefaultVal);
							pst.setInt(4, increaseStep);
							pst.setString(5, sSeqID);
							int resultUpdateMinMax = pst.executeUpdate();
							if (resultUpdateMinMax <= 0) {
								throw new Exception(
										"Cannot update oll_seq for min max changed. seq_id: "
												+ sSeqID + ", toMin: " + minVal
												+ ", toMax: " + maxVal);
							}
							close(pst, rs);
							String sqlUpdateOracleSeq = "alter sequence "
									+ settingOracleSeqId + " increment by "
									+ increaseStep + " minvalue " + minVal
									+ " maxvalue " + maxVal;
							pst = conn.prepareStatement(sqlUpdateOracleSeq);
							pst.execute();
						}
					}
				} else {
					// update sequence, check if value equals nMaximumVal, then
					// update to nDefault value
					retValue = currentValue + 1;
					long nextAvailableValue = currentValue
							+ (increaseStepVal > 0 ? increaseStepVal : 1);
					if (nMaximumVal > 0 && nextAvailableValue >= nMaximumVal) {
						// if meet maximum, then update to default start from
						// value
						sqlUpdate = "UPDATE oll_seq SET seq_val="
								+ (nextAvailableValue - nMaximumVal)
								+ ", max_val = " + nMaximumVal
								+ ", update_ts=sysdate WHERE seq_id = ?";
					} else {
						sqlUpdate = "UPDATE oll_seq SET seq_val=seq_val+"
								+ (increaseStepVal > 0 ? increaseStepVal : 1)
								+ ", max_val = " + nMaximumVal
								+ ", update_ts=sysdate WHERE seq_id = ?";
					}
					pst = conn.prepareStatement(sqlUpdate);
					pst.setString(1, sSeqID);
					int updateResult = pst.executeUpdate();
					if (updateResult <= 0)
						throw new Exception(
								"Update oll_seq failure, 0 updated.");
				}
			} else {

				// from 20150715, all new seq change to use oracle sequence.
				String oracleSeqId = "OLLSEQ_"
						+ ollSeqIdTsFmt.format(new Date());
				long maxVal = (nMaximumVal >= 0 ? nMaximumVal : 9999999999999L);
				long minVal = nDefaultVal;
				int increaseStep = 1;
				if (minVal > maxVal) {
					minVal = maxVal;
					maxVal = nDefaultVal;
					increaseStep = -1;
				}
				dbsettingIncrementBy = increaseStep;
				settingOracleSeqId = oracleSeqId;

				// if the sequence is not exist, then creeate it and return with
				// sDefault value
				String sqlAddNewSeq = "insert into oll_seq (seq_id, oracle_sequence_id, VAL_START_WITH, VAL_MINIMUM, VAL_MAXIMUM, VAL_INCREMENT_BY, update_ts) values (?, ?, ?, ?, ?, ?, sysdate)";
				pst = conn.prepareStatement(sqlAddNewSeq);
				pst.setString(1, sSeqID);
				pst.setString(2, oracleSeqId);
				pst.setLong(3, nDefaultVal);
				pst.setLong(4, minVal);
				pst.setLong(5, maxVal);
				pst.setInt(6, increaseStep);

				int insertResult = pst.executeUpdate();

				if (insertResult <= 0)
					throw new Exception("Insert oll_seq " + sSeqID
							+ " failure, 0 inserted.");

				// int increaseVal = (nMaximumVal>0 && nDefaultVal>0 &&
				// nDefaultVal>nMaximumVal)
				String sqlAddOracleSeq = "create sequence " + oracleSeqId
						+ " increment by " + increaseStep + " minvalue "
						+ minVal + " maxvalue " + maxVal + " start with "
						+ nDefaultVal + " CYCLE NOCACHE";
				pst = conn.prepareStatement(sqlAddOracleSeq);
				pst.execute();
				pst = conn.prepareStatement("GRANT SELECT ON " + oracleSeqId
						+ " TO B2B_OWNER");
				pst.execute();
				pst = conn.prepareStatement("GRANT SELECT ON " + oracleSeqId
						+ " TO B2B_SUPP");
				pst.execute();

				// -- END --- Insert Log into msglog_header

				pst = conn.prepareStatement("select " + oracleSeqId
						+ ".Nextval from dual");
				rs = pst.executeQuery();
				if (rs != null && rs.next()) {
					retValue = rs.getLong(1);
				} else {
					throw new Exception("Cannot get sequence for [" + sSeqID
							+ "], oracle sequence id: " + oracleSeqId
							+ ", pls check db.");
				}

			}

			// update increase step if it's not same as db setting 'increment
			// by'
			if (settingOracleSeqId.length() > 0 && increaseStepVal != 0
					&& increaseStepVal != dbsettingIncrementBy) {
				close(pst, rs);
				pst = conn.prepareStatement("alter sequence "
						+ settingOracleSeqId + " increment by "
						+ increaseStepVal);
				pst.execute();
				pst = conn.prepareStatement("select " + settingOracleSeqId
						+ ".nextval from dual");
				pst.execute();
				pst = conn.prepareStatement("alter sequence "
						+ settingOracleSeqId + " increment by "
						+ dbsettingIncrementBy);
				pst.execute();
			}

		} catch (Exception ex) {
			String err = "GetSequenceNextVal exception "
					+ "(sSeqID: "
					+ sSeqID
					+ ", nDefaultVal: "
					+ nDefaultVal
					+ ", nMaximumVal: "
					+ nMaximumVal
					+ (increaseStepVal == 0 ? "" : ", increaseStep: "
							+ increaseStepVal) + ") \r\n" + ex.getMessage();
			throw new Exception(err);
		} finally {
			close(pst, rs);
		}
		return retValue;
	}

	// / <summary>
	// / Get sequence current value
	// / </summary>
	// / <param name="sSeqID"></param>
	// / <returns></returns>
	public long GetSequenceCurrentVal(Connection conn, String sSeqID)
			throws Exception {
		if (sSeqID == null)
			return -1;
		long retLong = -1;

		String tsql = "SELECT seq_id, seq_val, update_ts FROM oll_seq WHERE seq_id = ?";
		tsql = "select s.seq_id sequence_id, u.sequence_name oracle_sequence_id, (u.last_number-u.increment_by) current_val, s.update_ts create_time from oll_seq s , user_sequences u where s.oracle_sequence_id=u.sequence_name and s.seq_id=?";

		ResultSet rs = null;
		PreparedStatement pst = null;

		try {
			if (conn == null)
				throw new Exception("DB Connection is null.");
			if (conn.isClosed())
				throw new Exception("DB Connection is closed.");

			pst = conn.prepareStatement(tsql);
			pst.setString(1, sSeqID);
			rs = pst.executeQuery();
			if (rs != null && rs.next()) {
				retLong = rs.getLong("current_val");
			} else {
				close(pst, rs);
				tsql = "SELECT seq_id, seq_val, update_ts FROM oll_seq WHERE seq_id = ?";
				pst = conn.prepareStatement(tsql);
				pst.setString(1, sSeqID);
				rs = pst.executeQuery();
				if (rs != null && rs.next()) {
					retLong = rs.getLong("seq_val");
				}
			}
		} catch (Exception ex) {
			String err = "GetSequenceCurrentVal Exception: \r\n" + "sSeqID: "
					+ sSeqID + ", \r\n" + ex.getMessage();
			throw new Exception(err);
		} finally {
			close(pst, rs);
		}
		return retLong;
	}

	private void close(PreparedStatement pst, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e1) {
			}
		}
		if (pst != null) {
			try {
				pst.close();
			} catch (Exception e1) {
			}
		}
	}

	public static void main(String[] args) {
		// TODO
	}
}
