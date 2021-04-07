# ReadMe
使用说明

奇怪的控件仓库

## widgets

### pieChartView
<img src="./markdownRes/示意图.png" width=300 />

二维饼图工具，可以画一个饼图，中间有缝隙，四周也有图例，特色是支持渐变色，可以选择一圈渐变，可以单个色块渐变，也可以不渐变。

目前支持以下api:

#### 设置旋转角度
> 0度是3点钟方向，默认的-90，如果需要旋转图形请在此设置旋转的角度
> 输入参数：float rotateAngle
```java
/**
* 设置旋转的角度
* 0度是3点钟方向，默认的-90，如果需要旋转图形请在此设置旋转的角度
* @param float rotateAngle 旋转的角度
*/
pieChartView.setRotateAngle(rotateAngle)


/**
* 是否显示图例
* @param showLegend
*/
public void setShowLegend(boolean showLegend)
```

初始化数据方式（目前仅支持动态添加数据，后续会有更新）：

> void updateData(List<Float> rateList, int[] colorList, List<String> nameList)
> public void updateData(List<Float> rateList, List<int[]> colorList, List<String> nameList)
```java
pieCharView.updateData()
```
