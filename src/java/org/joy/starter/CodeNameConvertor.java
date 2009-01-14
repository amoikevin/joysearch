/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.joy.starter;

/**
 *
 * @author Lamfeeling
 */
public class CodeNameConvertor {

    public static String getFriendlyName(String codeName) {
        if (codeName.equals("Apollo")) {
            return "检索集群管理器";
        }
        if (codeName.equals("Barton")) {
            return "检索服务器";
        }
        if (codeName.equals("Chirstmas")) {
            return "分析器集群管理器";
        }
        if (codeName.equals("Spartan")) {
            return "分析器";
        }
        if (codeName.equals("Sophie")) {
            return "蜘蛛集群管理器";
        }
        if (codeName.equals("Venus")) {
            return "蜘蛛";
        }
        if (codeName.equals("Odessay")) {
            return "网页链接分析器";
        }
        if (codeName.equals("Repository")) {
            return "网页内容缓存";
        }
        return null;
    }

    public static String getCodeName(String friendlyName) {
        if (friendlyName.equals("检索集群管理器")) {
            return "Apollo";
        }
        if (friendlyName.equals("检索服务器")) {
            return "Barton";
        }
        if (friendlyName.equals("分析器集群管理器")) {
            return "Chirstmas";
        }
        if (friendlyName.equals("分析器")) {
            return "Spartan";
        }
        if (friendlyName.equals("蜘蛛集群管理器")) {
            return "Sophie";
        }
        if (friendlyName.equals("蜘蛛")) {
            return "Venus";
        }
        if (friendlyName.equals("网页链接分析器")) {
            return "Odessay";
        }
        if (friendlyName.equals("网页内容缓存")) {
            return "Repository";
        }
        return null;
    }
}
