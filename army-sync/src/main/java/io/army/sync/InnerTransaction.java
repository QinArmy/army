package io.army.sync;

import io.army.tx.Transaction;

import java.sql.Statement;

interface InnerTransaction extends Transaction {

    Statement createStatement();
}
