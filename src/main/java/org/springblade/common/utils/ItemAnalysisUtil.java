package org.springblade.common.utils;

import org.apache.commons.lang.StringUtils;
import org.springblade.modules.mathmodel.entity.CastingOrderEntity;
import org.springblade.modules.mathmodel.entity.MailyMaterialTotalEntity;
import org.springblade.modules.mathmodel.entity.TubeMaterialInfoEntity;
import org.springblade.modules.pr.entity.ItemInfoEntityOfFL;
import org.springblade.modules.pr.entity.ItemInfoEntityOfQZ;
import org.springblade.modules.pr.entity.ItemInfoEntityOfXLJ;
import org.springblade.modules.pr.entity.ItemInfoEntityOfZDJ;
import org.springblade.modules.pr.vo.MaterialMaliyVO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemAnalysisUtil {

    private static String WCBTYPE="WCB,WCC,LCB,LCC,C5,C12,WC6";

    private static String CF8TYPE="CF8,CF3,CF8M,CF3M,CF8C,4A,5A";


    public static void main(String[] args) {



    }



    /**
     * 把物料描述拆解成物料描述类  -管棒料
     * @param itemName
     * @return
     */
    public static MaterialMaliyVO getItemInfoGuanBangLiao(String itemName){
        MaterialMaliyVO mailyList = new MaterialMaliyVO();
//        String name = "长管-80X102X163-E21-J45-PH-316L+G06";
        try {
            String[]  split = itemName.split("\\-");
            String name = split[0]; //品名
            String item = split[split.length-1];
            String[] splitItem = item.split("\\+");
            /*涂层   String tuceng = splitItem[1]; */
            String coating = splitItem[splitItem.length - 1];
            /*材质  String caizhi = splitItem[0];*/
            String  theMaterial= splitItem[splitItem.length - 2];
            String s = split[1]; //外径+内径+长度
            String[] number = s.split("x");
            Double length = Double.valueOf(number[number.length-1]);
            Double externalDiameter = Double.valueOf(number[number.length-2]);
            Double internalDiamete = Double.valueOf(number[number.length-3]);
            mailyList.setLength(length); //长度
            mailyList.setExternalDiameter(externalDiameter); //外径
            mailyList.setInternalDiamete(internalDiamete); //内径
            mailyList.setItemName(name); //品名
            mailyList.setCoating(coating); //涂层
            mailyList.setTheMaterial(theMaterial); //材质
        } catch (IllegalStateException e) {
            System.out.println("此物料不符合自动拆解功能");
            return mailyList;
        } finally {
            return mailyList;
        }
    }

    /**
     * 物料获取拆解
     * @param itemName
     * @return
     */
    public static TubeMaterialInfoEntity getEntity(String itemName){
        TubeMaterialInfoEntity mailyList = new TubeMaterialInfoEntity();
//        String name = "长管-80X102X163-E21-J45-PH-316L+G06";
        try {
            String[]  split = itemName.split("\\-");
            String name = split[0]; //品名
            String item = split[split.length-1];
            String[] splitItem = item.split("\\+");
            /*涂层   String tuceng = splitItem[1]; */
            String coating = splitItem[splitItem.length - 1];
            /*材质  String caizhi = splitItem[0];*/
            String  theMaterial= splitItem[splitItem.length - 2];
            String s = split[1]; //外径+内径+长度
            String[] number = s.split("x");

            Double length = Double.valueOf(number[number.length-1]);
            Double externalDiameter = Double.valueOf(number[number.length-2]);
            Double internalDiamete = Double.valueOf(number[number.length-3]);
            mailyList.setLength(length); //长度
            mailyList.setExternalDiameter(externalDiameter); //外径
            mailyList.setInternalDiamete(internalDiamete); //内径
            mailyList.setName(name); //品名
            mailyList.setCoating(coating); //涂层
            mailyList.setTheMaterial(theMaterial); //材质
        } catch (IllegalStateException e) {
//            return mailyList;
            throw new RuntimeException("拆解失败");
        } finally {
            return mailyList;
        }
    }

    /**
     * 物料拆解显示
     * @param itemName
     * @return
     */
    public static MailyMaterialTotalEntity getItemEntity(String itemName){
        MailyMaterialTotalEntity mailyList = new MailyMaterialTotalEntity();
//        String name = "长管-80X102X163-E21-J45-PH-316L+G06";
        try {
            String[]  split = itemName.split("\\-");
            String name = split[0]; //品名
            String item = split[split.length-1];
            String[] splitItem = item.split("\\+");
            /*涂层   String tuceng = splitItem[1]; */
            String coating = splitItem[splitItem.length - 1];
            /*材质  String caizhi = splitItem[0];*/
            String  theMaterial= splitItem[splitItem.length - 2];
            String s = split[1]; //外径+内径+长度
            String[] number = s.split("x");
            Double length = Double.valueOf(number[number.length-1]);
            Double externalDiameter = Double.valueOf(number[number.length-2]);
            Double internalDiamete = Double.valueOf(number[number.length-3]);
            mailyList.setLength(length); //长度
            mailyList.setExternalDiameter(externalDiameter); //外径
            mailyList.setInternalDiamete(internalDiamete); //内径
            mailyList.setName(name); //品名
            mailyList.setCoating(coating); //涂层
            mailyList.setTheMaterial(theMaterial); //材质
        } catch (IllegalStateException e) {
//            return mailyList;
            throw new RuntimeException("拆解失败");
        } finally {
            return mailyList;
        }
    }

    /**
     * 把物料描述拆解成物料描述类 - 球座
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfQZ getItemInfoOfQiuZuo(String itemName) {
        ItemInfoEntityOfQZ itemInfoEntity = new ItemInfoEntityOfQZ();
        try {
            String[] splitFrist = itemName.split("\\+");

            // **** 涂层 G06  ****
            String coat = splitFrist[1];

            // 球座组件-球体-2G6Y-PHA-Q50-X6-B-F316L
            String usedStr = splitFrist[0];

            // **** 物料分类 ****
            String itemize = usedStr.split("-")[0];

            // 切割 有效字符串数组
            String[] usedStrList = usedStr.split("-");

            // **** 材质 F316L ****
            String material = usedStrList[usedStrList.length-1];

            // **** 等级 B ****
            String grade = usedStrList[usedStrList.length-2];

            // 2G6Y
            String sizeAndPound = usedStrList[1];
            Pattern patternOfString = Pattern.compile("[a-zA-Z]");

            // 从 2G6Y 中 匹配第一个字母的位置，以获得 2（尺寸）
            Matcher matcherOfSize = patternOfString.matcher(sizeAndPound);
            matcherOfSize.find();
            // *** 尺寸 ****
            String size = sizeAndPound.substring(0, sizeAndPound.indexOf(matcherOfSize.group()));

            // 拆掉尺寸后 -> G6Y
            String poundAndOthers = sizeAndPound.substring(sizeAndPound.indexOf(matcherOfSize.group()));

            // 类别：G
            String form = poundAndOthers.substring(0,1);

            // **** 磅级 ****
            String pound = Pattern.compile("[^0-9]").matcher(poundAndOthers).replaceAll("").trim();

            // 其余的部分 PHA-Q50-X6
            /*List<String> usedStrStrList = Arrays.asList(usedStrList);
            List<String> newUsedStrStrList = new ArrayList<>(usedStrStrList);
            int oldSize = newUsedStrStrList.size();
            newUsedStrStrList.remove(oldSize-1);
            newUsedStrStrList.remove(oldSize-2);
            newUsedStrStrList.remove(0);
            newUsedStrStrList.remove(0);
            newUsedStrStrList.remove(0);

            // **** 特殊规则 ****
            String specialRule = "";
            if(usedStrStrList.contains("SZ")) {
                 specialRule = "SZ";
            } else if (usedStrStrList.contains("GP")) {
                 specialRule = "GP";
            } else {
                 specialRule = "";
            }*/
            String specialRule = "";
            itemInfoEntity.setItemize(itemize);
            itemInfoEntity.setForm(form);
            itemInfoEntity.setCoat(itemize.equals("球体")?coat:"");
            itemInfoEntity.setFzCoat(itemize.equals("阀座")?coat:"");
            itemInfoEntity.setSize(size);
            itemInfoEntity.setPound(pound);
            itemInfoEntity.setGrade(itemize.equals("阀座")?"":grade);
            itemInfoEntity.setMaterial(material);
            itemInfoEntity.setSpecialRule(specialRule);
        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }

    /**
     * 铸件自动下单报表
     * @param itemName
     * @return
     */
    public static CastingOrderEntity getItemInfoZhuJian(String itemName) {
        CastingOrderEntity order = new CastingOrderEntity();
//        ItemInfoEntityOfZDJ itemInfoEntity = new ItemInfoEntityOfZDJ();
        try {
            Pattern patternOfString = Pattern.compile("[a-zA-Z]");
            Pattern patternOfNumber = Pattern.compile("[0-9]");
            int first = itemName.indexOf("-") + 1;
            int last = itemName.lastIndexOf("-");
            String[] itemInfos = itemName.split("-");
            // 3B1RF-DPT-X1
            String itemInfoWithoutClassAndMaterial = itemName.substring(first, last);
            // 3B1RF
            String sizeFormPoundFlange = "";
            if(itemInfos.length > 3){
                sizeFormPoundFlange = itemInfoWithoutClassAndMaterial.substring(0, itemInfoWithoutClassAndMaterial.indexOf("-"));
            } else {
                sizeFormPoundFlange = itemInfoWithoutClassAndMaterial;
            }

            // 从 3B1RF 中 匹配第一个字母的位置，以获得 3（尺寸）
            Matcher matcherOfSize = patternOfString.matcher(sizeFormPoundFlange);
            matcherOfSize.find();
            // 拆掉尺寸后 -> B1RF
            String formPoundFlange = sizeFormPoundFlange.substring(sizeFormPoundFlange.indexOf(matcherOfSize.group()));
            // 从 B1RF 中 匹配 第一个数字的位置，以获得 B（形式）
            Matcher matcherOfForm = patternOfNumber.matcher(formPoundFlange);
            matcherOfForm.find();
            // 拆掉系列后 -> 1RF
            String poundFlange = formPoundFlange.substring(formPoundFlange.indexOf(matcherOfForm.group()));
            // 从 1RF 中 匹配 第一个字母的位置，以获得 1（磅级）
            Matcher matcherOfPound = patternOfString.matcher(poundFlange);
            // 拆掉磅级后 -> RF （法兰）
            String flangeStructure = "";
            String poundToUse = "";
            if(matcherOfPound.find()) {
                flangeStructure = poundFlange.substring(poundFlange.indexOf(matcherOfPound.group()));
                poundToUse = poundFlange.substring(0, poundFlange.indexOf(matcherOfPound.group()));;
            } else {
                poundToUse = poundFlange;
            }
            // 物料分类
            String itemClass = itemInfos[0];
            // 尺寸
            String size = sizeFormPoundFlange.substring(0, sizeFormPoundFlange.indexOf(matcherOfSize.group()));
            // 形式
            String form = formPoundFlange.substring(0, formPoundFlange.indexOf(matcherOfForm.group()));
            // 磅级
            String pound = poundToUse;
            // 法兰
            String flange = flangeStructure;
            // 系列
            String series = "";
            if(itemInfos.length > 3){
                String seriesToSplit = itemInfoWithoutClassAndMaterial.substring(itemInfoWithoutClassAndMaterial.indexOf("-") + 1);
                series = seriesToSplit;
            }
            // 材质 - 价格
            String itemMaterial = itemInfos[itemInfos.length - 1];
            // 特殊重复材质 再匹配
            List<String> WCBTypeList = Arrays.asList(WCBTYPE.split(","));
            List<String> CFTypeList = Arrays.asList(CF8TYPE.split(","));
            if(WCBTypeList.contains(itemMaterial)){
                // 单重 - 材质
                String itemMaterialOfWeight = "WCB";
                order.setMaterialOfWeight(itemMaterialOfWeight);
            } else if (CFTypeList.contains(itemMaterial)) {
                String itemMaterialOfWeight = "CF8";
                order.setMaterialOfWeight(itemMaterialOfWeight);
            }

            order.setItemize(itemClass);
            order.setItemSize(size);
            order.setForm(form);
            order.setPound(pound);
            order.setFlange(flange);
            order.setSeries(series);
            order.setMaterial(itemMaterial);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return order;
        } finally {
            return order;
        }
    }



    /**
     *
     * 把物料描述拆解成物料描述类 - 锻件
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfZDJ getItemInfoOfZhuDuanJian(String itemName) {
        ItemInfoEntityOfZDJ itemInfoEntity = new ItemInfoEntityOfZDJ();
        try {
            Pattern patternOfString = Pattern.compile("[a-zA-Z]");
            Pattern patternOfNumber = Pattern.compile("[0-9]");
            int first = itemName.indexOf("-") + 1;
            int last = itemName.lastIndexOf("-");
            String[] itemInfos = itemName.split("-");
            // 3B1RF-DPT-X1
            String itemInfoWithoutClassAndMaterial = itemName.substring(first, last);
            // 3B1RF
            String sizeFormPoundFlange = "";
            if(itemInfos.length > 3){
                 sizeFormPoundFlange = itemInfoWithoutClassAndMaterial.substring(0, itemInfoWithoutClassAndMaterial.indexOf("-"));
            } else {
                 sizeFormPoundFlange = itemInfoWithoutClassAndMaterial;
            }

            // 从 3B1RF 中 匹配第一个字母的位置，以获得 3（尺寸）
            Matcher matcherOfSize = patternOfString.matcher(sizeFormPoundFlange);
            matcherOfSize.find();
            // 拆掉尺寸后 -> B1RF
            String formPoundFlange = sizeFormPoundFlange.substring(sizeFormPoundFlange.indexOf(matcherOfSize.group()));
            // 从 B1RF 中 匹配 第一个数字的位置，以获得 B（形式）
            Matcher matcherOfForm = patternOfNumber.matcher(formPoundFlange);
            matcherOfForm.find();
            // 拆掉系列后 -> 1RF
            String poundFlange = formPoundFlange.substring(formPoundFlange.indexOf(matcherOfForm.group()));
            // 从 1RF 中 匹配 第一个字母的位置，以获得 1（磅级）
            Matcher matcherOfPound = patternOfString.matcher(poundFlange);
            // 拆掉磅级后 -> RF （法兰）
            String flangeStructure = "";
            String poundToUse = "";
            if(matcherOfPound.find()) {
                flangeStructure = poundFlange.substring(poundFlange.indexOf(matcherOfPound.group()));
                poundToUse = poundFlange.substring(0, poundFlange.indexOf(matcherOfPound.group()));;
            } else {
                poundToUse = poundFlange;
            }
            // 物料分类
            String itemClass = itemInfos[0];
            // 尺寸
            String size = sizeFormPoundFlange.substring(0, sizeFormPoundFlange.indexOf(matcherOfSize.group()));
            // 形式
            String form = formPoundFlange.substring(0, formPoundFlange.indexOf(matcherOfForm.group()));
            // 磅级
            String pound = poundToUse;
            // 法兰
            String flange = flangeStructure;
            // 系列
            String series = "";
            if(itemInfos.length > 3){
                String seriesToSplit = itemInfoWithoutClassAndMaterial.substring(itemInfoWithoutClassAndMaterial.indexOf("-") + 1);
                if (seriesToSplit.contains("DPT-X1")){
                    series = "DPT-X1";
                }else if (seriesToSplit.contains("F-X1")){
                    series = "F-X1";
                }else if (seriesToSplit.contains("FT-X1")){
                    series ="FT-X1";
                }else if (seriesToSplit.contains("RN-X1")){
                    series ="RN-X1";
                }else if (seriesToSplit.contains("X1")){
                    series ="X1";
                } else if (seriesToSplit.contains("X6")){
                    series ="X6";
                }
//                series = seriesToSplit;
//                // 去除 系列'X'后面的 -*
//                String[] seriesArray = seriesToSplit.split("-");
//                if (seriesArray.length > 1) {
//                    String seriesLast = seriesArray[seriesArray.length-1];
//                    if(seriesLast.indexOf("X") > -1) {
//                        series = seriesToSplit;
//                    } else{
//                        int end = seriesToSplit.lastIndexOf("-");
//                        series = seriesToSplit.substring(0,end);
//                    }
//                } else {
//                    if(seriesArray[0].indexOf("X") > -1){
//                        series = seriesToSplit;
//                    } else {
//                        return itemInfoEntity;
//                    }
//                }
            }
            // 材质 - 价格
            String itemMaterial = itemInfos[itemInfos.length - 1];
            // 特殊重复材质 再匹配
            List<String> WCBTypeList = Arrays.asList(WCBTYPE.split(","));
            List<String> CFTypeList = Arrays.asList(CF8TYPE.split(","));
            if(WCBTypeList.contains(itemMaterial)){
                // 单重 - 材质
                String itemMaterialOfWeight = "WCB";
                itemInfoEntity.setMaterialOfWeight(itemMaterialOfWeight);
            } else if (CFTypeList.contains(itemMaterial)) {
                String itemMaterialOfWeight = "CF8";
                itemInfoEntity.setMaterialOfWeight(itemMaterialOfWeight);
            }

            itemInfoEntity.setItemize(itemClass);  // 物料分类
            itemInfoEntity.setSize(size);  // 尺寸
            itemInfoEntity.setForm(form); // 形式
            itemInfoEntity.setPound(pound); // 磅级
            itemInfoEntity.setFlange(flange); // 法兰
            itemInfoEntity.setSeries(series); // 系列
            itemInfoEntity.setMaterial(itemMaterial);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }

    public static ItemInfoEntityOfZDJ getMaterialItemInfoOfDuanJian(String itemName) {
        ItemInfoEntityOfZDJ itemInfoEntity = new ItemInfoEntityOfZDJ();
        try {
            Pattern patternOfString = Pattern.compile("[a-zA-Z]");
            Pattern patternOfNumber = Pattern.compile("[0-9]");
            int first = itemName.indexOf("-") + 1;
            int last = itemName.lastIndexOf("-");
            String[] itemInfos = itemName.split("-");
            // 材质
            String itemMaterial = itemInfos[itemInfos.length - 1];
            itemInfoEntity.setMaterial(itemMaterial);
        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }

    /**
     * 把物料描述拆解成物料描述类 - 工序委外  international618
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfZDJ getItemInfoOfQCWW(String itemName) {
        ItemInfoEntityOfZDJ itemInfoEntity = new ItemInfoEntityOfZDJ();
        try {
            Pattern patternOfString = Pattern.compile("[a-zA-Z]");
            Pattern patternOfNumber = Pattern.compile("[0-9]");
            int first = itemName.indexOf("-") + 1;
            int last = itemName.lastIndexOf("-");
            String[] itemInfos = itemName.split("-");
            // 3B1RF-DPT-X1
            String itemInfoWithoutClassAndMaterial = itemName.substring(first, last);
            // 3B1RF
            String sizeFormPoundFlange = "";
            if(itemInfos.length > 3){
                sizeFormPoundFlange = itemInfoWithoutClassAndMaterial.substring(0, itemInfoWithoutClassAndMaterial.indexOf("-"));
            } else {
                sizeFormPoundFlange = itemInfoWithoutClassAndMaterial;
            }

            // 从 3B1RF 中 匹配第一个字母的位置，以获得 3（尺寸）
            Matcher matcherOfSize = patternOfString.matcher(sizeFormPoundFlange);
            matcherOfSize.find();
            // 拆掉尺寸后 -> B1RF
            String formPoundFlange = sizeFormPoundFlange.substring(sizeFormPoundFlange.indexOf(matcherOfSize.group()));
            // 从 B1RF 中 匹配 第一个数字的位置，以获得 B（形式）
            Matcher matcherOfForm = patternOfNumber.matcher(formPoundFlange);
            matcherOfForm.find();
            // 拆掉系列后 -> 1RF
            String poundFlange = formPoundFlange.substring(formPoundFlange.indexOf(matcherOfForm.group()));
            // 从 1RF 中 匹配 第一个字母的位置，以获得 1（磅级）
            Matcher matcherOfPound = patternOfString.matcher(poundFlange);
            // 拆掉磅级后 -> RF （法兰）
            String flangeStructure = "";
            String poundToUse = "";
            if(matcherOfPound.find()) {
                flangeStructure = poundFlange.substring(poundFlange.indexOf(matcherOfPound.group()));
                poundToUse = poundFlange.substring(0, poundFlange.indexOf(matcherOfPound.group()));;
            } else {
                poundToUse = poundFlange;
            }
            // 物料分类
            String itemClass = itemInfos[0];
            // 尺寸
            String size = sizeFormPoundFlange.substring(0, sizeFormPoundFlange.indexOf(matcherOfSize.group()));
            // 形式
            String form = formPoundFlange.substring(0, formPoundFlange.indexOf(matcherOfForm.group()));
            // 磅级
            String pound = poundToUse;
            // 法兰
            String flange = flangeStructure;
            // 系列
            String series = "";

            if(itemInfos.length > 3){
                 String  seriesToSplit = itemInfoWithoutClassAndMaterial.substring(itemInfoWithoutClassAndMaterial.indexOf("-") + 1);
                if (seriesToSplit.contains("DPT-X1")){
                    series = "DPT-X1";
                }else if (seriesToSplit.contains("F-X1")){
                    series = "F-X1";
                }else if (seriesToSplit.contains("FT-X1")){
                    series ="FT-X1";
                }else if (seriesToSplit.contains("X1")){
                    series ="X1";
                } else if (seriesToSplit.contains("X6")){
                    series ="X6";
                }
//             series = seriesToSplit;
            }
            // 材质 - 价格
            String itemMaterial = itemInfos[itemInfos.length - 1];
            // 特殊重复材质 再匹配
            List<String> WCBTypeList = Arrays.asList(WCBTYPE.split(","));
            List<String> CFTypeList = Arrays.asList(CF8TYPE.split(","));
            if(WCBTypeList.contains(itemMaterial)){
                // 单重 - 材质
                String itemMaterialOfWeight = "WCB";
                itemInfoEntity.setMaterialOfWeight(itemMaterialOfWeight);
            } else if (CFTypeList.contains(itemMaterial)) {
                String itemMaterialOfWeight = "CF8";
                itemInfoEntity.setMaterialOfWeight(itemMaterialOfWeight);
            }

            itemInfoEntity.setItemize(itemClass); // 物料分类
            itemInfoEntity.setSize(size);// 尺寸
            itemInfoEntity.setForm(form); // 形式
            itemInfoEntity.setPound(pound); // 磅级
            itemInfoEntity.setFlange(flange); // 法兰
            itemInfoEntity.setSeries(series); //系列
            itemInfoEntity.setMaterial(itemMaterial); //材质-价格

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }






    /**
     * 把物料描述拆解成物料描述类 - 法兰
     * 例：连接法兰-20WN63RF-C-J-F304
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfFL getItemInfoOfFL(String itemName) {
        ItemInfoEntityOfFL itemInfoEntity = new ItemInfoEntityOfFL();
        //连接法兰-20WN63RF-C-J-F304
        //连接法兰-20WN3RF-C-J-F304
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // F304
            String cz = splitFrist[splitFrist.length-1];//材质

            // 20WN63RF
            String usedStr = splitFrist[1];


            String flxs = ""; //法兰形式

            if(usedStr.indexOf("PL")>-1){
                flxs="PL";

            } else if (usedStr.indexOf("WN")>-1) {
                flxs="WN";
            }else {
                //默认就是BL
                flxs="BL";
            }
            String[] usedStr2 = usedStr.split(flxs);

            String cj=usedStr2[0];//寸级



            itemInfoEntity.setSize(cj);
            itemInfoEntity.setForm(flxs);
            itemInfoEntity.setMaterial(cz);

            if(usedStr2.length>1){
                itemInfoEntity.setPound(usedStr2[1]);
                if(StringUtils.isNotBlank(usedStr2[1])&&usedStr2[1].length()>2){
                    String substring = usedStr2[1].substring(usedStr2[1].length() - 2, usedStr2[1].length());
                    itemInfoEntity.setLjxs(substring);
                }else{
                    itemInfoEntity.setLjxs("");
                }
            }else{
                itemInfoEntity.setLjxs("");
            }



        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }


    public static int NumberSize(String str) {

        str=str.trim();

        String str2="";

        if(str != null && !"".equals(str)){
            for(int i=0;i<str.length();i++){
                if(str.charAt(i)>=48 && str.charAt(i)<=57){
                    str2+=str.charAt(i);
                }
            }
        }
        return str2.length();
    }

    /**
     * 把物料描述拆解成物料描述类 - 弹簧座
     * 例：碟簧座-49x30x54x26.3-630
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfTHZ(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        // 碟簧座-49x30x54x26.3-630
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质  630****
            String material = splitFrist[splitFrist.length-1];

            // 49x30x54x26.3
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            String outersize = userdStrList[0]; //外径
            String innersize = userdStrList[1]; //内径
            String longSize = userdStrList[2]; //高度

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setInnerSize(innersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setMaterial(material);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }


    /**
     * 把物料描述拆解成物料描述类 - 挡块
     * 例：挡块-60.8x11.9-630
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfDK(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        // 挡块-60.8x11.9-630
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质  630****
            String material = splitFrist[splitFrist.length-1];

            // 60.8x11.9
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            String outersize = userdStrList[0]; //外径
            String longSize = userdStrList[1]; //高度

            itemInfoEntity.setMainType(partName);

            itemInfoEntity.setInnerSize("0");
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setMaterial(material);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }


    /**
     * 把物料描述拆解成物料描述类 - 压环
     * 例：压环-92x100x5.1-X6-316
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfYH(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        // 压环-92x100x5.1-X6-316
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质  316****
            String material = splitFrist[splitFrist.length-1];

            // **** 系列  X6****
            String series = splitFrist[2];

            // 92x100x5.1
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            String outersize = userdStrList[1]; //外径
            String innersize = userdStrList[0]; //内径
            String longSize = userdStrList[2]; //高度

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setInnerSize(innersize);
            itemInfoEntity.setMaterial(material);
            itemInfoEntity.setSeries(series);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }

    /**
     * 把物料描述拆解成物料描述类 - 填料压套
     * 例：填料压套-45.5x65x22-X6-304
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfTLYT(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        // 填料压套-45.5x65x22-X6-304
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质  316****
            String material = splitFrist[splitFrist.length-1];

            // **** 系列  X6****
            String series = splitFrist[2];

            // 92x100x5.1
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            String outersize = userdStrList[1]; //外径
            String innersize = userdStrList[0]; //内径
            String longSize = userdStrList[2]; //高度

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setInnerSize(innersize);
            itemInfoEntity.setMaterial(material);
            itemInfoEntity.setSeries(series);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }


    /**
     * 把物料描述拆解成物料描述类 - 填料隔环
     * 例：填料隔环-86x105x65-316
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfTLGH(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质****
            String material = splitFrist[splitFrist.length-1];

            // 86x105x65
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            String outersize = userdStrList[1]; //外径
            String innersize = userdStrList[0]; //内径
            String longSize = userdStrList[2]; //高度

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setInnerSize(innersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setMaterial(material);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }


    /**
     * 把物料描述拆解成物料描述类 - 支撑套
     * 例：支撑套-100x149x79.5-20
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfZCT(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质****
            String material = splitFrist[splitFrist.length-1];

            // 100x149x79.5
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            String outersize = userdStrList[1]; //外径
            String innersize = userdStrList[0]; //内径
            String longSize = userdStrList[2]; //高度

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setInnerSize(innersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setMaterial(material);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }

    /**
     * 把物料描述拆解成物料描述类 - 长管
     * 例：长管-343x402x177-J30-20
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfCG(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质****
            String material = splitFrist[splitFrist.length-1];

            // 343x402x177
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            String outersize = userdStrList[1]; //外径
            String innersize = userdStrList[0]; //内径
            String longSize = userdStrList[2]; //高度

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setInnerSize(innersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setMaterial(material);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }

    /**
     * 把物料描述拆解成物料描述类 - 短管
     * 例：短管-31x43x30.3-20
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfDG(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质****
            String material = splitFrist[splitFrist.length-1];

            // 31x43x30.3
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            String outersize = userdStrList[1]; //外径
            String innersize = userdStrList[0]; //内径
            String longSize = userdStrList[2]; //高度

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setInnerSize(innersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setMaterial(material);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }


    /**
     * 把物料描述拆解成物料描述类 - 接管
     * 例：接管-51x8x360-K35-20
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfJG(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质****
            String material = splitFrist[splitFrist.length-1];

            // 31x43x30.3
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            String biHou = userdStrList[1]; //壁厚
            String outersize = userdStrList[0]; //外径
            String longSize = userdStrList[2]; //高度
            String innersize = new BigDecimal(outersize).subtract(new BigDecimal(biHou).multiply(new BigDecimal("2"))).toString();

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setBiHou(biHou);
            itemInfoEntity.setInnerSize(innersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setMaterial(material);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }

    /**
     * 把物料描述拆解成物料描述类 - 填料垫
     * 例：填料垫-110x135x4-316L
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfTLD(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质****
            String material = splitFrist[splitFrist.length-1];

            // 31x43x30.3
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            String outersize = userdStrList[1]; //外径
            String innersize = userdStrList[0]; //内径
            String longSize = userdStrList[2]; //高度

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setInnerSize(innersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setMaterial(material);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }

    /**
     * 把物料描述拆解成物料描述类 - 凸管
     * 例：凸管-71x92x60-20
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfTG(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质****
            String material = splitFrist[splitFrist.length-1];

            // 71x92x60
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            String outersize = userdStrList[1]; //外径
            String innersize = userdStrList[0]; //内径
            String longSize = userdStrList[2]; //高度

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setInnerSize(innersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setMaterial(material);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }

    /**
     * 把物料描述拆解成物料描述类 - 衬管
     * 例：衬管-201x235x25-316
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfChenG(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质****
            String material = splitFrist[splitFrist.length-1];

            // 201x235x25
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            String outersize = userdStrList[1]; //外径
            String innersize = userdStrList[0]; //内径
            String longSize = userdStrList[2]; //高度

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setInnerSize(innersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setMaterial(material);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }


    /**
     * 把物料描述拆解成物料描述类 - 填料压板
     * 例：填料压板-105x155x30-YD-X6-304
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfTLYB(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        try {
            if(itemName.indexOf("YD")>-1) {
                String[] splitFrist = itemName.split("-");
                // 零件名称
                String partName = splitFrist[0];

                // **** 材质****
                String material = splitFrist[splitFrist.length-1];

                // 105x155x30
                String usedStr = splitFrist[1];
                String[] userdStrList = usedStr.split("x");
                String outersize = userdStrList[1]; //外径
                String innersize = userdStrList[0]; //内径
                String longSize = userdStrList[2]; //高度

                itemInfoEntity.setMainType(partName);
                itemInfoEntity.setOuterSize(outersize);
                itemInfoEntity.setInnerSize(innersize);
                itemInfoEntity.setHeightSize(longSize);
                itemInfoEntity.setMaterial(material);
                itemInfoEntity.setMaterialType("棒料");
            } else {
                String[] splitFrist = itemName.split("-");
                // 零件名称
                String partName = splitFrist[0];

                // **** 材质****
                String material = splitFrist[splitFrist.length-1];

                // 32x63x15
                String usedStr = splitFrist[1];
                String[] userdStrList = usedStr.split("x");
                String outersize = userdStrList[1]; //长度（L)
                String longSize = userdStrList[2]; //厚度（T)

                itemInfoEntity.setMainType(partName);
                itemInfoEntity.setOuterSize(outersize);
                itemInfoEntity.setHeightSize(longSize);
                itemInfoEntity.setMaterial(material);
                itemInfoEntity.setMaterialType("板料");
            }
        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }


    /**
     * 把物料描述拆解成物料描述类 - 凸板
     * 例：填料压板-105x155x30-YD-X6-304
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfTB(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质****
            String material = splitFrist[splitFrist.length-1];

            // 32x63x15
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            if(userdStrList.length==1) {
                userdStrList = usedStr.split("X");
            }
            String outersize = userdStrList[1]; //长度（L)
            String longSize = userdStrList[2]; //厚度（T)

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setMaterial(material);
            itemInfoEntity.setMaterialType("板料");

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }


    /**
     * 把物料描述拆解成物料描述类 - 蝶阀下盖
     * 例：蝶阀下盖-F45-113x75x26-K4x16-F316
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfDFXG(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质****
            String material = splitFrist[splitFrist.length-1];

                // F45-113x75x26-K4x16
            String usedStr = splitFrist[2];
            String[] userdStrList = usedStr.split("x");
            String outersize = userdStrList[0]; //底长（L)
            String innersize = userdStrList[1]; //底宽（W）
            String longSize = userdStrList[2]; //厚度 (T)

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setInnerSize(innersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setMaterial(material);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }

    /**
     * 把物料描述拆解成物料描述类 - 联轴器
     * 例：联轴器-K2Ax72x220x55-X6-45+Zn
     * @param itemName
     * @return
     */
    public static ItemInfoEntityOfXLJ getItemInfoOfLZQ(String itemName) {
        ItemInfoEntityOfXLJ itemInfoEntity = new ItemInfoEntityOfXLJ();
        try {
            String[] splitFrist = itemName.split("-");
            // 零件名称
            String partName = splitFrist[0];

            // **** 材质****
            String material = splitFrist[splitFrist.length-1].substring(0,splitFrist[splitFrist.length-1].indexOf("+"));


            // 联轴器的长宽是手动输入的
            String usedStr = splitFrist[1];
            String[] userdStrList = usedStr.split("x");
            String outersize = ""; //外径
            String innersize = ""; //内径
            String longSize = userdStrList[2];  //高度自动获取

            itemInfoEntity.setMainType(partName);
            itemInfoEntity.setOuterSize(outersize);
            itemInfoEntity.setInnerSize(innersize);
            itemInfoEntity.setHeightSize(longSize);
            itemInfoEntity.setMaterial(material);

        } catch (IllegalStateException e) {
            System.out.println("此物料号不符合自动拆解功能");
            return itemInfoEntity;
        } finally {
            return itemInfoEntity;
        }
    }

    public static ItemInfoEntityOfXLJ getItemInfoOfXLJ(String itemName) {
        if (itemName.indexOf("碟簧座") > -1) {
            return getItemInfoOfTHZ(itemName);
        } else if (itemName.indexOf("挡块") > -1) {
            return getItemInfoOfDK(itemName);
        } else if (itemName.indexOf("压环") > -1) {
            return getItemInfoOfYH(itemName);
        } else if (itemName.indexOf("填料压套") > -1) {
            return getItemInfoOfTLYT(itemName);
        } else if (itemName.indexOf("填料隔环") > -1) {
            return getItemInfoOfTLGH(itemName);
        } else if (itemName.indexOf("支撑套") > -1) {
            return getItemInfoOfZCT(itemName);
        } else if (itemName.indexOf("长管") > -1) {
            return getItemInfoOfCG(itemName);
        } else if (itemName.indexOf("短管") > -1) {
            return getItemInfoOfDG(itemName);
        } else if (itemName.indexOf("接管") > -1) {
            return getItemInfoOfJG(itemName);
        } else if (itemName.indexOf("填料垫") > -1) {
            return getItemInfoOfTLD(itemName);
        } else if (itemName.indexOf("凸管") > -1) {
            return getItemInfoOfTG(itemName);
        } else if (itemName.indexOf("蝶阀下盖") > -1) {
            return getItemInfoOfDFXG(itemName);
        } else if (itemName.indexOf("衬管") > -1) {
            return getItemInfoOfChenG(itemName);
        } else if (itemName.indexOf("填料压板") > -1) {
            return getItemInfoOfTLYB(itemName);
        } else if (itemName.indexOf("凸板") > -1) {
            return getItemInfoOfTB(itemName);
        }else if (itemName.indexOf("联轴器") > -1) {
            return getItemInfoOfLZQ(itemName);
        }
        return null;
    }
}
