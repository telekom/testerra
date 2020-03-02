/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
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
 *
 * Contributors:
 *     Peter Lehmann
 *     pele
 */

package eu.tsystems.mms.tic.testframework.mailconnector.util;

/** Set the SSL Configuration with ThreadLocal KeyChains. */
public final class SSLConfig {

    /** ThreadLocal Object to only set one threadsafe keystore key chain */
    private static ThreadLocal<KeyChain> currentKeyStoreKeyChain = new ThreadLocal<KeyChain>();

    /** ThreadLocal Object to only set one threadsafe keystore key chain */
    private static ThreadLocal<KeyChain> currentTrustStoreKeyChain = new ThreadLocal<KeyChain>();

    /** Private constructor to hide the public one since this is a static only class. */
    private SSLConfig() { }

    /**
     * Set the keystore keychain.
     *
     * @param keyChain
     *              The KeyChain to set.
     */
    public static void setCurrentKeyStoreKeyChain(final KeyChain keyChain) {
        currentKeyStoreKeyChain.set(keyChain);
    }

    /**
     * Get the keystore keycahin
     *
     * @return KeyChain
     */
    public static KeyChain getCurrentKeyStoreKeyChain() {
        return currentKeyStoreKeyChain.get();
    }

    /**
     * Set the truststore keychain.
     *
     * @param keyChain
     *              The KeyChain to set.
     */
    public static void setCurrentTrustStoreKeyChain(final KeyChain keyChain) {
        currentTrustStoreKeyChain.set(keyChain);
    }

    /**
     * Get the truststore keychain
     *
     * @return KeyChain
     */
    public static KeyChain getCurrentTrustStoreKeyChain() {
        return currentTrustStoreKeyChain.get();
    }

}
