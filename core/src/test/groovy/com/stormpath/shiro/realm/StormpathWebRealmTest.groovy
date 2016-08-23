/*
 * Copyright 2012 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.shiro.realm

import com.stormpath.sdk.account.Account
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher
import org.testng.annotations.Test

import static org.testng.Assert.assertFalse
import static org.testng.Assert.assertNotNull
import static org.testng.Assert.assertNull
import static org.testng.Assert.assertSame
import static org.testng.Assert.assertTrue
import static org.easymock.EasyMock.*
import static org.hamcrest.Matchers.*
import static org.hamcrest.MatcherAssert.*

/**
 * Tests for {@link StormpathWebRealm}.
 */
class StormpathWebRealmTest {

    @Test
    void testDefaultInstance() {

        def realm = new StormpathWebRealm()
        assertTrue realm.getCredentialsMatcher() instanceof AllowAllCredentialsMatcher //allow all - Stormpath will do the credentials comparison as necessary
        assertTrue realm.getGroupRoleResolver() instanceof DefaultGroupRoleResolver
        assertTrue realm.getGroupPermissionResolver() instanceof GroupCustomDataPermissionResolver
        assertTrue realm.getAccountPermissionResolver() instanceof AccountCustomDataPermissionResolver
        assertNull realm.getAccountRoleResolver()
    }

    @Test
    void testAuthTokenType() {
        assertFalse new StormpathWebRealm().supports(new UsernamePasswordToken("username", "password"))
        assertTrue new StormpathWebRealm().supports(new StormpathWebRealm.AccountAuthenticationToken(null))
    }

    @Test
    void testSuccessAuthc() {

        def href = "http://accountHref"

        def account = createMock(Account)
        expect(account.href).andReturn(href).times(2)
        expect(account.getUsername()).andReturn("username")
        expect(account.getEmail()).andReturn("email")
        expect(account.getGivenName()).andReturn("givenName")
        expect(account.getMiddleName()).andReturn("middleName")
        expect(account.getSurname()).andReturn("surname")

        replay account

        def realm = new StormpathWebRealm()
        def token = new StormpathWebRealm.AccountAuthenticationToken(account)
        def authenticationInfo = realm.doGetAuthenticationInfo(token)

        assertNotNull authenticationInfo
        assertThat authenticationInfo.principals.asSet(), allOf(hasSize(2), hasItem(href))
        assertSame href, authenticationInfo.getPrincipals().primaryPrincipal

        verify account
    }

    @Test(expectedExceptions = [AuthenticationException])
    void testFailureAuthc() {

        def realm = new StormpathWebRealm()
        def token = new StormpathWebRealm.AccountAuthenticationToken(null)
        realm.doGetAuthenticationInfo(token)

    }
}
