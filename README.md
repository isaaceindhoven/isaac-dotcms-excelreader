# dotCMS Excel Reader

If you want to display information that is stored in an Excel sheet on your [dotCMS](https://www.dotcms.com/) website, this is the plugin for you. The Excel Reader plugin can read .xls and .xlsx files for you, return it in a nice object and store it in the dotCMS cache. Reading Excel sheets just became very easy and fast!

Previous releases of this plugin for older dotCMS versions can be found [here](../../releases).

## Features

* Read .xls and .xlsx files.
* Return .xls and .xlsx files as objects.
* Store .xls and .xlsx files in the dotCMS cache.

## Installation

To use it, you obviously first need to install the plugin. To install it take these steps:

* Clone this repository.
* Open the console and go the the folder containing pom.xml
* Execute the following maven command: **mvn clean package**
* The build should succeed and a folder named "target" will be created.
* Open the "target" folder and check if the **.jar** file exists.
* Open dotCMS and go to the dotCMS Dynamic Plugins page by navigating to "System" > "Dynamic Plugins".
* Click on "Upload Plugin" and select the .jar file located in the "/target" folder.
* Click on "Upload Plugin" and the plugin should install and automatically start.
* To check if the plugin is working, visit the following URL and replace **your-url-here** with your own site address: **http://your-url-here/app/servlets/monitoring/isaac-dotcms-excelreader**

That's it, you've installed the plugin and can use the excel reader.

## Usage

After deploying the plugin you can use it by calling the methods provided by the ExcelReaderTool class (viewtool). With the **$excel** reference you can access the methods in velocity.

```
#set($rowList = $excel.readExcel('d:\data\clients.xls'))
#set($rowList2 = $excel.readExcelFromDotCMS('\global\excel\clients.xlsx'))
```

The excel sheet (.xls or .xlsx) is read, stored into the cache and returned as a list of Maps. The first row of the excel sheet should contain the column names, the other rows the data. To display the excel sheet data you can use the following code:

```
<table>
#foreach($row in $rowList)
  <tr><td>$row.get('name')</td><td>$row.get('email')</td></tr>
#end
</table>
```

To check the header names you can use the following code:

```
$rowList.get(0).keySet()
```

All the fields are returned as Objects, so even dates shouldn't pose a problem.

## Tips & Tricks

Here are a few more 'hard core' features that you might be interested in.

#### Sorting and filtering the data

There are some extra features in this plugin to help you to display the excel data in a somewhat nicer way, by doing some server-side filtering and sorting:

```java
#set($contacts = $excel.readExcel('d:\temp\contacts.xlsx'))
#set($contacts = $excel.filter($contacts, "city", "eindhoven"))
#set($dummy = $excel.sort($contacts, "name"))
```

This code first filters the fields in the column 'city' and checks for the String 'eindhoven'. To check this, the plugin converts the values to String (if they aren't already) and checks them ignoring the case. The sorting is done in about the same way, by converting the values in the column 'name' to String and sorting the strings.

#### Excel parser software

The excel reader plugin uses [Apache POI](http://poi.apache.org) to parse the files.

## Meta

[ISAAC - 100% Handcrafted Internet Solutions](https://www.isaac.nl) – [@ISAAC](https://twitter.com/isaaceindhoven) – [info@isaac.nl](mailto:info@isaac.nl)

Distributed under the [Creative Commons Attribution 3.0 Unported License](https://creativecommons.org/licenses/by/3.0/).
