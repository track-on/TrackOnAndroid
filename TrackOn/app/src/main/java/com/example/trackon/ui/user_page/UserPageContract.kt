package com.example.trackon.ui.user_page

import com.example.trackon.model.data.ReportItem
import com.example.trackon.model.data.Token
import com.example.trackon.model.data.User

class UserPageContract {
    interface View {
        fun loadReports(reportItem: ArrayList<ReportItem>)
        fun retryLoadReports()
        fun tokenError()
        fun gotoLogin()
        fun setNewToken(token: Token)
        fun setUser(user: User)
        fun retrySetUser()
    }

    interface Presenter {
        fun getMyReports(token: String)
        fun retryGetMyReports(token: String)
        fun getUser(token: String)
        fun retryGetUser(token: String)
        fun refreshToken(token: String)
    }
}