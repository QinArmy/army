package io.army.criteria.mysql;

import io.army.criteria.NamedWindow;
import io.army.criteria.Query;


public interface MySQLNamedWindow extends NamedWindow {


    interface Window80Spec<C> extends NamedWindow.ExistingWindowClause<C, PartitionSpec<C>>, PartitionSpec<C> {

    }

    interface PartitionSpec<C> extends NamedWindow.PartitionClause<C, Order80Spec<C>>, Order80Spec<C> {

    }

    interface Order80Spec<C> extends Query.OrderByClause<C, FrameUnit80Spec<C>>, FrameUnit80Spec<C> {

    }

    interface FrameUnit80Spec<C> extends NamedWindow.FrameUnitClause<C, FrameExtent80Spec<C>> {

    }

    interface FrameExtent80Spec<C> extends FrameBetween80Spec<C>, FrameStart80Spec<C> {

    }

    interface FrameStart80Spec<C> extends NamedWindow.FrameClause<C, NamedWindow> {

    }

    interface FrameBetween80Spec<C>
            extends NamedWindow.FrameBetweenClause<NamedWindow.FrameClause<C, FrameBetweenAnd80Spec<C>>> {

    }

    interface FrameBetweenAnd80Spec<C>
            extends NamedWindow.FrameBetweenAndClause<NamedWindow.FrameClause<C, NamedWindow>> {

    }


}
