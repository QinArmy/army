package io.army.criteria.impl;

import io.army.criteria.Selection;

/**
 * package interface
 *
 * @since 1.0
 */
interface AnonymousSelection extends Selection {

    /**
     * @throws UnsupportedOperationException always
     */
    @Override
    String label();


}
