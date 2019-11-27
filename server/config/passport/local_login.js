
var LocalStrategy = require('passport-local').Strategy;

module.exports = new LocalStrategy({
		usernameField : 'email',
		passwordField : 'password',
		passReqToCallback : true   // 이 옵션을 설정하면 아래 콜백 함수의 첫번째 파라미터로 req 객체 전달됨
	}, function(req, email, password, done) { 
		console.log('passport의 local-login 호출됨 : ' + email + ', ' + password);
		console.log(req.body);
		var database = req.app.get('database');
	    database.UserModel.findOne({ 'email' :  email }, function(err, user) {
	    	if (err) { return done(err); }

	    	// 등록된 사용자가 없는 경우
	    	if (!user) {
	    		console.log('계정이 일치하지 않음.');
	    		return done(null, false, req.flash('loginMessage', 'email을 확인해주세요.'));  // 검증 콜백에서 두 번째 파라미터의 값을 false로 하여 인증 실패한 것으로 처리
	    	}
	    	
	    	// 비밀번호 비교하여 맞지 않는 경우
			var authenticated = user.authenticate(password, user._doc.salt, user._doc.hashed_password);
			if (!authenticated) {
				console.log('비밀번호 일치하지 않음.');
				return done(null, false, req.flash('loginMessage', 'password를 확인해주세요.'));  // 검증 콜백에서 두 번째 파라미터의 값을 false로 하여 인증 실패한 것으로 처리
			}
			if (user._doc.authentication == "false")
			{
			    console.log('email 인증 하지 않음.');
			    return done(null, false, req.flash('loginMessage','email 인증을 완료해주세요.'));
			}
			// 정상인 경우
			console.log('계정과 비밀번호가 일치함.');
			console.log(Date.now());
			user.updated_at=Date.now(); // last login 시간을 기억한다.
			// req.body.token 이 null 이면 안된다.
			// req.body.token 이 값이 있어야하고 값이 있을때 
			if(req.body.token){
				if(user.token!=req.body.token){
					user.token=req.body.token;
				}
			}
			user.save(function(err){
				if(err)
				{
					throw err;
				}
			});
			return done(null, user);  // 검증 콜백에서 두 번째 파라미터의 값을 user 객체로 넣어 인증 성공한 것으로 처리
	    });

	});

