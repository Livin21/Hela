package com.lmntrx.android.hela

import java.io.Serializable


/***
 * Created by Livin Mathew <mail@livinmathew.me> on 25/3/18.
 */

data class User(val fname: String?,val lname: String?,val phoneNumber: String?,val emailId: String?,val dob: String?,val tobaccoUser: Int,val city: String?,val state: String?): Serializable