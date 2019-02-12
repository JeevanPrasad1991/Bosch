package bosch.cpm.com.bosch.multiselectionspin;
import java.util.ArrayList;


/**
 * Created by jeevanp on 2/8/2018.
 */


import bosch.cpm.com.bosch.gsonGetterSetter.BrandMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.CategoryMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.StoreCategoryMaster;
public interface ProductSpinnerListener {
    void onItemsSelectedProduct(ArrayList<CategoryMaster> items);
}
