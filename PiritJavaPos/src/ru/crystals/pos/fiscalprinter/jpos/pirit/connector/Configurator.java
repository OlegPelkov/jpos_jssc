package ru.crystals.pos.fiscalprinter.jpos.pirit.connector;

public class Configurator
{
  private String portName = "COM3";
  private String baudRate = "57600";
  private String dataBits = "8";
  private String stopBits = "1";
  private String parity = "NONE";
  private String useCTS = "YES";
  private String depart = "1";
  private String password = "32323";

  public String getPortName()
  {
    return this.portName;
  }

  public void setPortName(String portName) {
    this.portName = portName;
  }

  public String getBaudRate() {
    return this.baudRate;
  }

  public void setBaudRate(String baudRate) {
    this.baudRate = baudRate;
  }

  public String getDataBits() {
    return this.dataBits;
  }

  public void setDataBits(String dataBits) {
    this.dataBits = dataBits;
  }

  public String getStopBits() {
    return this.stopBits;
  }

  public void setStopBits(String stopBits) {
    this.stopBits = stopBits;
  }

  public String getParity() {
    return this.parity;
  }

  public void setParity(String parity) {
    this.parity = parity;
  }

  public String getUseCTS() {
    return this.useCTS;
  }

  public void setUseCTS(String useCTS) {
    this.useCTS = useCTS;
  }

  public String getDepart() {
    return this.depart;
  }

  public void setDepart(String depart) {
    this.depart = depart;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}