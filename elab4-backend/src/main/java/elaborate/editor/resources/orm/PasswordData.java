package elaborate.editor.resources.orm;

public class PasswordData {
	String token;
	private String newpassword;
	private String emailAddress;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getNewPassword() {
		return newpassword;
	}

	public void setNewPassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

}
