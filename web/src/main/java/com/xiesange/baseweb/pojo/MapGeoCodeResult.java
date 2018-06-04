package com.xiesange.baseweb.pojo;
/**
 * 通过调用相关地图API接口返回的查询结果
 * @author Wilson Wu
 * @date 2015年9月22日
 *
 */
public class MapGeoCodeResult {
	private String text;//地点名称
	private String place_name;//地点详细地址
	private Double[] center;//地点经纬度,第一个为经度，第二个为纬度
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getPlace_name() {
		return place_name;
	}
	public void setPlace_name(String place_name) {
		this.place_name = place_name;
	}
	public Double[] getCenter() {
		return center;
	}
	public void setCenter(Double[] center) {
		this.center = center;
	}
}
