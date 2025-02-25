package pe.goblin.gateway.context;

/**
 * <h4>NOTE</h4>
 * <p>
 * this token is signed with rsa under directory {@link pe.goblin.gateway.context.rsa}.
 * rsa in this directory must "only" be used for test.
 * Be aware that the iss is "http://localhost:8080/realms/myrealm", which means this token is only valid when 'spring.security.oauth2.resourceserver.jwt.issuer-uri' is equal to 'http://localhost:8080/realms/myrealm'.
 * Otherwise, spring-security-oauth2-resource-server will find this token invalid.
 * </p>
 * <h4>format(RSA_SIGNED_TOKEN)</h4>
 * <h5>Header</h5>
 * {
 * "alg": "RS256",
 * "typ": "JWT"
 * }
 * <h5>Payload</h5>
 * {
 * "exp": 4000000000,
 * "iat": 1740441600,
 * "jti": "51ecf963-b390-4d68-9c8d-8d1c8969e9bc",
 * "iss": "http://localhost:8080/realms/myrealm",
 * "sub": "be7c3d0c-d00e-498f-a71b-5e4cace54be2",
 * "typ": "Serialized-ID",
 * "sid": "3a05c71e-b45a-4055-91cb-d655be20f8b8",
 * "state_checker": "S2W-KloLdu2WLDP5X_LdniUd74mwDP1z_VMXmUbzt60"
 * }
 */
public interface TokenFixture {
    String RSA_SIGNED_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjQwMDAwMDAwMDAsImlhdCI6MTc0MDQ4NDEwNSwianRpIjoiNTFlY2Y5NjMtYjM5MC00ZDY4LTljOGQtOGQxYzg5NjllOWJjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9teXJlYWxtIiwic3ViIjoiYmU3YzNkMGMtZDAwZS00OThmLWE3MWItNWU0Y2FjZTU0YmUyIiwidHlwIjoiU2VyaWFsaXplZC1JRCIsInNpZCI6IjNhMDVjNzFlLWI0NWEtNDA1NS05MWNiLWQ2NTViZTIwZjhiOCIsInN0YXRlX2NoZWNrZXIiOiJTMlctS2xvTGR1MldMRFA1WF9MZG5pVWQ3NG13RFAxel9WTVhtVWJ6dDYwIn0.iMImTvOMDfoFG49FDFqNp2c74uWvG1xDQL8hDIeYuvRUYQulL68W8Ipz9E3JmYkHC2owpw38hlNSklyAU8TLfuzcLwWUgxDTbiPJQ22054Wn9NElGkhLuPtyOkvjxpjAnjxCyHFagBx_wR1Ebuu7kS5zSX1yxarYnJFtBHf8G8IV-G49IR3dy-LZs3eWdXW3Z8xK3fndiXHCV1dc6rWRaoIAXuzykv46Ilk-7X70Scyrhen_MXyQHVD3SdV9gmN4SC--8djxpGLOmnz-kYrOIrUsCK_CbURKrx88zHgrf-BvDQYS2XXnj-hsxKCHOex5XFGrlL8xVyqF4_HKIWIYbA";
    String RSA_SIGNED_EXPIRED_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3NDA0NDE3MDAsImlhdCI6MTc0MDQ0MTYwMCwianRpIjoiNTFlY2Y5NjMtYjM5MC00ZDY4LTljOGQtOGQxYzg5NjllOWJjIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9teXJlYWxtIiwic3ViIjoiYmU3YzNkMGMtZDAwZS00OThmLWE3MWItNWU0Y2FjZTU0YmUyIiwidHlwIjoiU2VyaWFsaXplZC1JRCIsInNpZCI6IjNhMDVjNzFlLWI0NWEtNDA1NS05MWNiLWQ2NTViZTIwZjhiOCIsInN0YXRlX2NoZWNrZXIiOiJTMlctS2xvTGR1MldMRFA1WF9MZG5pVWQ3NG13RFAxel9WTVhtVWJ6dDYwIn0.jWXyGMTE_9Z0SK8W2q91x63q3zfP3ZIsBx_rXA45DkHM2BAqDzmU3dSp229swKnJwRduFInTAU9zIIv9M0n-HNHxJTLbmYxAuVTi0EOUkPGChA077uTXDYLnYsb_FGvbU7-dSFNQpDW2lUicUERW0YgzFtOOnEr74C3Eup24nwhktqErO77TaDn3-0l_FTtiqPeTXitHqDrr5NV7uDi5ypytC0CB1DlZ7KX2uXjdLGjBbWETM-RfDaHHTBTMiPX3V-GPo9VVw2-1V5iOlKVsF0X_ETUTM3ItkK_48BmX03lx2G81Nsg3dfXE6cDPtXJ8rwI8xKcN-6RCp9tRR5zoKQ";
    String MALFORM_TOKEN = "some malformed token";
}
