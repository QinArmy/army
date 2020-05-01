package io.army.boot;

import io.army.tx.Transaction;

import java.sql.Statement;

interface InnerTransaction extends Transaction {

    Statement createStatement();
}
