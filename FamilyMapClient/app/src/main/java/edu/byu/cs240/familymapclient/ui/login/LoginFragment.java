package edu.byu.cs240.familymapclient.ui.login;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ReqRes.LoginRequest;
import ReqRes.LoginResult;
import ReqRes.RegisterRequest;
import ReqRes.RegisterResult;
import ReqRes.SinglePersonResult;
import edu.byu.cs240.familymapclient.DataCache;
import edu.byu.cs240.familymapclient.R;
import edu.byu.cs240.familymapclient.ServerProxy;

public class LoginFragment extends Fragment{
    private Listener listener;

    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextHost;
    private EditText editTextPort;
    private EditText editTextFirst;
    private EditText editTextLast;
    private EditText editTextEmail;
    private RadioGroup radioGroup;
    private RadioButton maleButton;
    private RadioButton femaleButton;
    Button signInButton;
    Button registerButton;

    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    private static class LoginTask implements Runnable{
        private final Handler messageHandler;
        private String username;
        private String pw;
        private String host;
        private String port;
        public LoginTask(Handler messageHandler, String username, String pw, String host, String port){
            this.username = username;
            this.pw = pw;
            this.messageHandler = messageHandler;
            this.host = host;
            this.port = port;
        }
        @Override
        public void run() {
            ServerProxy server = new ServerProxy(host, port);
            LoginRequest request = new LoginRequest(username, pw);
            LoginResult result = server.login(request);
            server.getPeople();
            server.getEvents();
            SinglePersonResult personResult = server.getPerson(result.getPersonID());
            DataCache cache = DataCache.getInstance();
            cache.setFilters(cache.getPersonsMap().get(result.getPersonID()).getPersonID(), cache.getPersonsMap().get(result.getPersonID()).getFatherID(),
                    cache.getPersonsMap().get(result.getPersonID()).getMotherID(), host, port);
            cache.setEventsByPerson();
            sendMessage(result, personResult);
        }
        private void sendMessage(LoginResult result, SinglePersonResult personResult){
            Message message = Message.obtain();
            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean("LOGIN_SUCCESS", result.isSuccess());
            messageBundle.putString("FIRST_NAME", personResult.getFirstName());
            messageBundle.putString("LAST_NAME", personResult.getLastName());
            message.setData(messageBundle);
            messageHandler.sendMessage(message);
        }
    }

    private static class RegisterTask implements Runnable{
        private String username;
        private String pw;
        private String host;
        private String port;
        private String firstName;
        private String lastName;
        private String email;
        private String gender;
        private final Handler messageHandler;

        public RegisterTask(Handler messageHandler, String username, String pw, String host, String port,
                            String firstName, String lastName, String email, String gender){
            this.username = username;
            this.pw = pw;
            this.host = host;
            this.port = port;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.gender = gender;
            this.messageHandler = messageHandler;
        }


        @Override
        public void run() {
            ServerProxy server = new ServerProxy(host, port);
            RegisterRequest request = new RegisterRequest(username, pw, email, firstName, lastName, gender, null);
            RegisterResult result = server.register(request);
            server.getPeople();
            server.getEvents();
            SinglePersonResult person = server.getPerson(result.getPersonID());
            DataCache cache = DataCache.getInstance();
            cache.setFilters(cache.getPersonsMap().get(result.getPersonID()).getPersonID(), cache.getPersonsMap().get(result.getPersonID()).getFatherID(),
                    cache.getPersonsMap().get(result.getPersonID()).getMotherID(), host, port);
            cache.setEventsByPerson();
            sendMessage(result, person);
        }
        private void sendMessage(RegisterResult result, SinglePersonResult person){
            Message message = Message.obtain();
            Bundle messageBundle = new Bundle();
            messageBundle.putBoolean("LOGIN_SUCCESS", result.isSuccess());
            messageBundle.putString("FIRST_NAME", person.getFirstName());
            messageBundle.putString("LAST_NAME", person.getLastName());
            message.setData(messageBundle);
            messageHandler.sendMessage(message);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        signInButton = (Button) view.findViewById(R.id.signInButton);
        registerButton = view.findViewById(R.id.registerButton);
        editTextUsername = view.findViewById(R.id.username);
        editTextPassword = view.findViewById(R.id.password);
        editTextHost = view.findViewById(R.id.serverHost);
        editTextPort = view.findViewById(R.id.serverPort);
        editTextFirst = view.findViewById(R.id.firstName);
        editTextLast = view.findViewById(R.id.lastName);
        editTextEmail = view.findViewById(R.id.email);

        radioGroup = view.findViewById(R.id.genderGroup);
        maleButton = view.findViewById(R.id.radio_male);
        femaleButton = view.findViewById(R.id.radio_female);

        editTextUsername.addTextChangedListener(loginTextWatcher);
        editTextPassword.addTextChangedListener(loginTextWatcher);
        editTextPort.addTextChangedListener(loginTextWatcher);
        editTextHost.addTextChangedListener(loginTextWatcher);

        editTextUsername.addTextChangedListener(registerTextWatcher);
        editTextPassword.addTextChangedListener(registerTextWatcher);
        editTextPort.addTextChangedListener(registerTextWatcher);
        editTextHost.addTextChangedListener(registerTextWatcher);
        editTextFirst.addTextChangedListener(registerTextWatcher);
        editTextLast.addTextChangedListener(registerTextWatcher);
        editTextEmail.addTextChangedListener(registerTextWatcher);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message message){
                        Bundle bundle = message.getData();

                        if(bundle.getBoolean("LOGIN_SUCCESS", false)){
                            String firstName = bundle.getString("FIRST_NAME");
                            String lastName = bundle.getString("LAST_NAME");
                            String firstAndLast = firstName + " " + lastName;
                            Toast.makeText(getActivity(), firstAndLast, Toast.LENGTH_SHORT).show();
                            if(listener != null){
                                listener.notifyDone();
                            }
                        }
                        else{
                            Toast.makeText(getActivity(), R.string.failString, Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                EditText usernameEditText = getView().findViewById(R.id.username);
                String usernameString = usernameEditText.getText().toString();

                EditText pwEditText = getView().findViewById(R.id.password);
                String pwString = pwEditText.getText().toString();

                EditText hostEditText = getView().findViewById(R.id.serverHost);
                String hostString = hostEditText.getText().toString();

                EditText portEditText = getView().findViewById(R.id.serverPort);
                String portString = portEditText.getText().toString();
                LoginTask task = new LoginTask(uiThreadMessageHandler, usernameString, pwString, hostString, portString);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(task);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()){
                    @Override
                    public void handleMessage(Message message){
                        Bundle bundle = message.getData();

                        if(bundle.getBoolean("LOGIN_SUCCESS", false)){
                            String firstName = bundle.getString("FIRST_NAME");
                            String lastName = bundle.getString("LAST_NAME");
                            String firstAndLast = firstName + " " + lastName;
                            Toast.makeText(getActivity(), firstAndLast, Toast.LENGTH_SHORT).show();
                            if(listener != null){
                                listener.notifyDone();
                            }
                        }
                        else{
                            Toast.makeText(getActivity(), R.string.failString, Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                DataCache cache = DataCache.getInstance();
                editTextUsername = getView().findViewById(R.id.username);
                String username = editTextUsername.getText().toString();

                editTextPassword = getView().findViewById(R.id.password);
                String password = editTextPassword.getText().toString();

                editTextHost = getView().findViewById(R.id.serverHost);
                String host = editTextHost.getText().toString();


                editTextPort = getView().findViewById(R.id.serverPort);
                String port = editTextPort.getText().toString();

                editTextEmail = getView().findViewById(R.id.email);
                String email = editTextEmail.getText().toString();

                editTextFirst = getView().findViewById(R.id.firstName);
                String firstName = editTextFirst.getText().toString();

                editTextLast = getView().findViewById(R.id.lastName);
                String lastName = editTextLast.getText().toString();

                int selectedID = radioGroup.getCheckedRadioButtonId();
                String gender = null;
                if(selectedID == R.id.radio_male){
                    gender = "m";
                }
                if(selectedID == R.id.radio_female){
                    gender = "f";
                }

                RegisterTask task = new RegisterTask(uiThreadMessageHandler, username, password, host,
                        port, firstName, lastName, email, gender);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(task);
            }
        });
        
        return view;
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String usernameInput = editTextUsername.getText().toString();
            String passwordInput = editTextPassword.getText().toString();
            String hostInput = editTextHost.getText().toString();
            String portInput = editTextPort.getText().toString();
            signInButton.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty() &&
                    !hostInput.isEmpty() && !portInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    private TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String usernameInput = editTextUsername.getText().toString();
            String passwordInput = editTextPassword.getText().toString();
            String hostInput = editTextHost.getText().toString();
            String portInput = editTextPort.getText().toString();
            String firstName = editTextFirst.getText().toString();
            String lastName = editTextLast.getText().toString();
            String email = editTextEmail.getText().toString();
            registerButton.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty() &&
                    !hostInput.isEmpty() && !portInput.isEmpty() && !firstName.isEmpty() &&
                    !lastName.isEmpty() && !email.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    
}