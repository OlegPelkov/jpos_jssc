package ru.crystals.pos.fiscalprinter.jpos.pirit.connector;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

public class DataPacket
{
  public static final Logger Log = Logger.getLogger(DataPacket.class);
  private StringBuilder data;
  private final String FS = "\u001C";

  public DataPacket()
  {
    this.data = new StringBuilder();
  }

  public DataPacket(String data)
  {
    this.data = new StringBuilder();
    this.data.append(data);
  }

  public void clear() {
    this.data = new StringBuilder();
  }

  public String getDataBuffer() {
    return this.data.toString();
  }

  public int getCountValue() {
    int beginIndex = 0;
    int endIndex = this.data.indexOf(FS, beginIndex);
    int count = 0;

    while (endIndex >= beginIndex) {
      beginIndex = endIndex + 1;
      endIndex = this.data.indexOf(FS, beginIndex);
      ++count;
    }

    return count;
  }

  public int getLength() {
    return this.data.length();
  }

  public void putStringValue(String value) {
    if (value != null)
      this.data.append(value + FS);
    else
      this.data.append(FS);
  }

  public String getStringValue(int Index) throws Exception
  {
    int beginIndex = 0;
    int endIndex = this.data.indexOf(FS, beginIndex);
    int tmpIndex = 0;
    Log.info("Data:"+this.data);
    Log.info("beginIndex:"+beginIndex);
    Log.info("endIndex:"+endIndex);
    while (endIndex >= beginIndex) {
      if (tmpIndex == Index)
        return this.data.substring(beginIndex, endIndex);

      beginIndex = endIndex + 1;
      endIndex = this.data.indexOf(FS, beginIndex);
      ++tmpIndex;
    }

    throw new Exception("Error parse of data: Index=" + Index + " out of bound");
  }

  public void putDateValue(Date value) {
    DateFormat df = new SimpleDateFormat("ddMMyy");
    this.data.append(df.format(value) + FS);
  }

  public Date getDateValue(int Index) throws Exception {
    DateFormat df = new SimpleDateFormat("ddMMyy");
    return df.parse(getStringValue(Index));
  }

  public String getDateValueFullFormat() throws Exception {
    DateFormat df = new SimpleDateFormat("ddMMyy");
    Date resultDate = df.parse(getStringValue(0));
    df = new SimpleDateFormat("ddMMyyyyhhmm");
    String result = df.format(resultDate);
    String time = data.substring(7,11);
    result = result.substring(0,8);
    return result + time;
  }

  public void putTimeValue(Date value) {
    DateFormat df = new SimpleDateFormat("HHmmss");
    this.data.append(df.format(value) + FS);
  }

  public Date getTimeValue(int Index) throws Exception {
    DateFormat df = new SimpleDateFormat("HHmmss");
    return df.parse(getStringValue(Index));
  }

  public void putLongValue(Long value) {
    if (value != null)
      this.data.append(value + FS);
    else
      this.data.append(FS);
  }

  public long getLongValue(int Index) throws Exception
  {
    return Long.parseLong(getStringValue(Index));
  }

  public void putDoubleValue(double value) {
    this.data.append(String.format("%.3f", new Object[] { Double.valueOf(value) }).replace(',', '.') + FS);
  }

  public double getDoubleValue(int Index) throws Exception {
    return Double.parseDouble(getStringValue(Index));
  }
}