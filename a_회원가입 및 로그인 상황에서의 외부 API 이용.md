### 1. 사용자(비회원)가 앱을 처음 실행했을 경우

![](https://github.com/hyunbin1/NAWA_Android/blob/docs/imgs/a1.png?raw=true)

1. 사용자가 앱을 실행하면 메인 화면인 클럽 조회 레이아웃에 접근하게 된다.
2. 이 페이지에서는 dp api에 접근하여 db에 있는 클럽들을 보여준다.
3. 회원이 참여한 클럽 및 주변 클럽은 비회원에게 보여주지 않는다.
4. 사용자가 클럽 컴포넌트를 클릭하게 되면 webview를 통해서 클럽 상세보기를 지원한다.

### 2. 마이페이지로 접근한 경우

![](https://github.com/hyunbin1/NAWA_Android/blob/docs/imgs/a2.png?raw=true)

1. 사용자는 비회원이기 때문에 마이페이지 접속시 로그인 및 회원가입을 권유하는 레이아웃을 보여준다.
2. 회원가입 버튼을 누르면 회원가입에 필요한 정보를 입력할 수 있는 페이지를 보여준다.
3. 거주지 관련 정보를 입력할 때 카카오 api를 이용하여 데이터를 저장할 수 있도록 한다.
4. 정보를 입력한 후에 EditText에 작성된 text는 db api를 이용하여 작성된 정보를 user table에 저장한다.(서버 컨트롤러의 api를 이용해도 됨( ex. post("auth/register") ))

### 3. 회원가입 후 로그인

![](https://github.com/hyunbin1/NAWA_Android/blob/docs/imgs/a3.png?raw=true)

1. 로그인 후 메인페이지인 클럽 조회 페이지를 보여준다.
2. 참여한 클럽 및 주변 클럽 정보를 조회할 수 있다.
