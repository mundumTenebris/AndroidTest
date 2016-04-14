package ru.androidtest.interactionIterface;

import ru.androidtest.dataProviders.AbstractDataProvider;
import ru.androidtest.dataProviders.AbstractExpandableDataProvider;

/**
 * Created by blank on 30.12.2015.
 */
public interface OnInteractionListener {
    AbstractExpandableDataProvider getExpandableDataProvider();

    AbstractDataProvider getDataProvider();
}