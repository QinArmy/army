package io.army.session.executor;

import io.army.session.CloseableSpec;
import io.army.session.OptionSpec;

public interface StmtExecutorFactory extends CloseableSpec, OptionSpec {


    /**
     * @return true : underlying database driver provider save point spi.
     */
    boolean supportSavePoints();

    /**
     * <p>For example:
     * <ul>
     *     <li>io.jdbd</li>
     *     <li>java.sql</li>
     * </ul>
     *
     * @return driver spi vendor,The value returned typically is the package name for this vendor.
     */
    String driverSpiVendor();


}
