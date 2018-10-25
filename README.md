![image alt text](/images/nopassword_logo.png)

# NoPassword Workstation Authentication

The NoPassword Workstation node allows ForgeRock users to integrate their AM instance to the NoPassword authentication services.
This document assumes that you already have an AM 5.5+ instance running with an users base configured.

## Installation

Follow this steps in order to install the node:

1. Download the jar file from [here](https://raw.githubusercontent.com/NoPasswordRepo/NoPasswordOpenAMWorkstation/master/target/nopassword-openam-workstation-auth-1.0.jar).
2. Copy the **nopassword-workstation-agent-1.0.jar** file on your server: `/path/to/tomcat/webapps/openam/WEB-INF/lib`
3. Restart AM.
4. Login into NoPassword admin portal and open the `Keys` menu on the left side. Copy the **NoPassword Login** value by clicking in the green button and save it for later.

![image alt text](/images/nopassword_login_key.png)

5. Login into AM console as an administrator and go to `Realms > Top Level Real > Authentication > Trees`.
6. Click on **Add Tree** button. Name the tree NoPasswordWorkstation and click **Create**.

![image](/images/add_tree.png)

7. Add 3 tree nodes: Start, Username Collector, NoPassword Workstation.
8. Connect them as shown in the image below.

![image](/images/tree_1.png)

9. Select the **NoPassword Workstation** node and set the NoPassword Login Key. Paste the key value from step 4 on **NoPassword Login Key**. 
10. Set the following URL `https://api.nopassword.com/auth/webauth` in **Web Auth Endpoint**.
11. Set the following URL `http://HOSTNAME:PORT/openam/XUI/?realm=/#login/&service=NoPasswordWorkstation` in **Redirect URL**, replace HOSTNAME and PORT values.
12. Set the following URL `https://api.nopassword.com/Auth/WebAuthCheckToken` in **Check Token Endpoint**.
13. Add Success and Failure nodes, and connect them as shown in the image below.

![image](/images/tree_2.png)

14. Save changes.
15. You can test the NoPassword authentication tree by accessing this URL in your browser `https://HOSTNAME:PORT/openam/XUI/?realm=/#login/&service=NoPasswordWorkstation`.</br>
16. Enter your username and hit enter. **NoPassword AM Module will search for user email (mail or email attribute) in the data store if email is empty an email address will be generated from user DN**. The browser will be redirected to NoPassword and open the workstation agent for authentication. If the user is authenticated it will be redirected back to AM.