package bosch.cpm.com.bosch.multiselectionspin;

import java.util.ArrayList;

import bosch.cpm.com.bosch.gsonGetterSetter.BrandMaster;
import bosch.cpm.com.bosch.gsonGetterSetter.StoreCategoryMaster;

/**
 * Created by jeevanp on 2/2/2018.
 */

public interface SpinnerListener {
    void onItemsSelected(ArrayList<BrandMaster> items);
}

