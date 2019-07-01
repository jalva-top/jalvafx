# jalvafx
JavaFx library contains node utility classes and controller interfaces.
Library support two languages (English and Russian). To add your locale support you should create file **resources/jalvafxConstants_[locale].properties** with dictionary. See examples in **resources/jalvafxConstants_en.properties** and  **resources/jalvafxConstants_ru.properties** .

## Usage
#### ComboBoxCustomizer:

![](https://github.com/jalva-top/jalvafx/blob/master/image/ComboBoxCustomizer.png)

```
List<String> items = Arrays.asList("One", "Two", "Three", "Four", "Five", "Six");
ComboBox<String> comboBox = new ComboBox<>();
comboBox.getItems().addAll(items);

ComboBoxCustomizer.create(comboBox)
                  .emphasized(o -> o.startsWith("F"))
		  .multyColumn(o -> Arrays.asList(
						   "item index = " + items.indexOf(o), 
					           o == null ? "" : "hashCode = " + o.hashCode()
				    ))
		  .customize();
```
**By default, Double click to clear ComboBox value**