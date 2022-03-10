from rest_framework import status
from rest_framework.decorators import api_view, permission_classes, authentication_classes
from rest_framework.permissions import AllowAny, IsAuthenticated, IsAdminUser
from rest_framework.pagination import PageNumberPagination
from rest_framework.response import Response

from rest_framework_simplejwt.tokens import RefreshToken
from rest_framework_simplejwt.authentication import JWTAuthentication

from django.conf import settings
from django.contrib.auth import authenticate
from django.contrib.auth.hashers import check_password
from django.contrib.auth.models import update_last_login
from django.shortcuts import get_object_or_404, redirect

from dj_rest_auth.registration.views import SocialLoginView

from accounts.serializers import UserSerializer, UserInfoSerializer

from allauth.socialaccount.models import SocialAccount
from allauth.socialaccount.providers.oauth2.client import OAuth2Client
from allauth.socialaccount.providers.google import views as google_view

from accounts.models import User
from django.http import JsonResponse
import requests

from drf_yasg.utils import swagger_auto_schema
from drf_yasg import openapi

# 이메일 관련 import
from .email import account_activation_token, message
from django.utils.http import urlsafe_base64_encode, urlsafe_base64_decode
from django.utils.encoding import force_bytes, force_text
import string
import random
from django.core.mail import EmailMessage

from .parser import get_serializer

BASE_URL = getattr(settings, 'BASE_URL', None)

@swagger_auto_schema(
    method='get',
    operation_id='회원 정보 상세 조회(유저)',
    operation_description='회원 정보를 조회 합니다',
    tags=['유저'],
    responses={status.HTTP_200_OK: UserInfoSerializer},
)
@api_view(['GET'])
@permission_classes([IsAuthenticated])
@authentication_classes([JWTAuthentication])
def user_detail(request):
    serializer = UserSerializer(request.user)
    
    return Response(serializer.data, status=status.HTTP_200_OK)

page = openapi.Parameter('page', openapi.IN_QUERY, default=1,
                        description="페이지 번호", type=openapi.TYPE_INTEGER)
size = openapi.Parameter('size', openapi.IN_QUERY, default=5,
                        description="한 페이지에 표시할 객체 수", type=openapi.TYPE_INTEGER)
sort = openapi.Parameter('sort', openapi.IN_QUERY, default="id",
                        description="정렬할 기준 Column, 'id'면 오름차순 '-id'면 내림차순", type=openapi.TYPE_STRING)
@swagger_auto_schema(
    method='get',
    operation_id='회원 정보 전체 조회(어드민)',
    operation_description='회원 정보를 전체를 조회 합니다',
    tags=['유저'],
    manual_parameters=[page, size, sort],
    responses={200: openapi.Response(
        description="200 OK",
        schema=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'count': openapi.Schema(type=openapi.TYPE_STRING, description="전체 회원 수"),
                'next': openapi.Schema(type=openapi.TYPE_STRING, description="다음 조회 페이지 주소"),
                'previous': openapi.Schema(type=openapi.TYPE_STRING, description="이전 조회 페이지 주소"),
                'results' : get_serializer(UserInfoSerializer, "유저 정보"),
            }
        )
    )}
)
@api_view(['GET'])
@permission_classes([IsAuthenticated, IsAdminUser])
@authentication_classes([JWTAuthentication])
def user_list(request):
    sort = request.GET.get('sort')
    
    if not sort == None:
        user_list = User.objects.all().order_by(sort) # 전달 받은 값 기준 정렬
    else:
        user_list = User.objects.all()
        
    paginator = PageNumberPagination()
    
    page_size = request.GET.get('size')
    if not page_size == None:
        paginator.page_size = page_size
        
    result = paginator.paginate_queryset(user_list, request)
    serializers = UserInfoSerializer(result, many=True)
    return paginator.get_paginated_response(serializers.data)

@swagger_auto_schema(
    method='post',
    operation_id='비밀번호 확인(유저)',
    operation_description='비밀번호를 확인합니다',
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        properties={
            'password': openapi.Schema(type=openapi.TYPE_STRING, description="확인할 비밀번호"),
        }
    ),
    tags=['유저'],
    responses={200: openapi.Response(
        description="200 OK",
        schema=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'password_check': openapi.Schema(type=openapi.TYPE_BOOLEAN, default=True, description="비밀번호 확인"),
            }
        )
    ),
            409: openapi.Response(
        description="409 이미 생성된 이메일",
        schema=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'password_check': openapi.Schema(type=openapi.TYPE_BOOLEAN, default=False, description="비밀번호 확인"),
            }
        )
    )}
)
@api_view(['POST'])
@permission_classes([IsAuthenticated])
@authentication_classes([JWTAuthentication])
def password_check(request):
    user = request.user
    password = request.data.get('password')
    
    if not check_password(password, user.password):
        return Response({'password_check': 'False'}, status=status.HTTP_409_CONFLICT)
    
    return Response({'password_check': 'True'}, status=status.HTTP_200_OK)
    
@swagger_auto_schema(
    method='put',
    operation_id='회원정보 수정(유저)',
    operation_description='회원정보를 수정합니다',
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        properties={
            'name': openapi.Schema(type=openapi.TYPE_STRING, description="변경할 이름"),
            'new_password': openapi.Schema(type=openapi.TYPE_STRING, description="변경할 비밀번호, 변경 안할시 값 X"),
            'profile_img': openapi.Schema(type=openapi.TYPE_FILE, description="변경할 프로필 이미지 파일"),
        }
    ),
    tags=['유저'],
    responses={200: ""}
)
@api_view(['PUT'])
@permission_classes([IsAuthenticated])
@authentication_classes([JWTAuthentication])
def user_update(request, pk):
    new_password = request.data.get('new_password')
    user = get_object_or_404(User, pk=pk)
    
    serializer = UserInfoSerializer(instance=user, data=request.data)
    
    if serializer.is_valid(raise_exception=True):
        user = serializer.save()
        if new_password:
            user.set_password(new_password)
            user.save()
            
        return Response(status=status.HTTP_200_OK)

@swagger_auto_schema(
    method='delete',
    operation_id='회원 삭제(유저)',
    operation_description='회원정보를 제거합니다',
    tags=['유저'],
    responses={200: ""}
)    
@api_view(['DELETE'])
@permission_classes([IsAuthenticated])
@authentication_classes([JWTAuthentication])
def user_delete(request, pk):
    user = get_object_or_404(User, pk=pk)
    user.delete()
    return Response(status=status.HTTP_200_OK)

@swagger_auto_schema(
    method='post',
    operation_id='일반 회원가입(아무나)',
    operation_description='회원가입을 진행합니다',
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        properties={
            'email': openapi.Schema(type=openapi.TYPE_STRING, description="이메일"),
            'password': openapi.Schema(type=openapi.TYPE_STRING, description="비밀번호"),
            'name': openapi.Schema(type=openapi.TYPE_STRING, description="이름"),
        }
    ),
    tags=['유저'],
    responses={201: ""}
)
@api_view(['POST'])
@permission_classes([AllowAny])
def signup(request):
    email = request.data.get('email')
    password = request.data.get('password')
    name = request.data.get('name')

    serializer = UserSerializer(data=request.data)
    serializer.email = email
    serializer.name = name

    if serializer.is_valid(raise_exception=True):
        user = serializer.save()
        user.set_password(password)
        user.save()
        
        # 이메일 인증 전송
        uidb64 = urlsafe_base64_encode(force_bytes(user.pk))
        token = account_activation_token.make_token(user)
        message_data = message(uidb64, token)
        
        mail_title = "JRstock 이메일 인증 안내"
        mail_to = email
        send_email = EmailMessage(mail_title, message_data, to=[mail_to])
        send_email.content_subtype = "html"
        send_email.send()
        # 여기까지

        return Response(status=status.HTTP_201_CREATED)
    
@swagger_auto_schema(
    method='get',
    operation_id='이메일 중복 확인(아무나)',
    operation_description='이메일 중복 확인을 진행합니다',
    tags=['유저'],
    responses={200: openapi.Response(
        description="200 OK",
        schema=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'email_check': openapi.Schema(type=openapi.TYPE_BOOLEAN, default=True, description="중복 확인"),
            }
        )
    ),
            409: openapi.Response(
        description="409 이미 생성된 이메일",
        schema=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'email_check': openapi.Schema(type=openapi.TYPE_BOOLEAN, default=False, description="중복 확인"),
            }
        )
    )}
)
@api_view(['GET'])
@permission_classes([AllowAny])
def email_check(request, email):
    try:
        user = User.objects.get(email=email)
    except:
        user = None
        
    if user is None:
        return Response({'email_check': 'True'}, status=status.HTTP_200_OK)
    
    return Response({'email_check' : 'False'}, status=status.HTTP_409_CONFLICT)

@swagger_auto_schema(
    method='get',
    operation_id='이메일 인증(아무나)',
    operation_description='이메일 인증 처리를 진행합니다',
    tags=['유저'],
    responses={status.HTTP_200_OK: ""}
)
@api_view(['GET'])
@permission_classes([AllowAny])
def email_confirm(request, **kwargs):
    uidb64 = kwargs['uidb64']
    token = kwargs['token']
    uid = force_text(urlsafe_base64_decode(uidb64))
    user = get_object_or_404(User, pk=uid)
    
    if account_activation_token.check_token(user, token):
        user.is_active = True
        user.save()
        
        return redirect(BASE_URL)

@swagger_auto_schema(
    method='post',
    operation_id='일반 로그인(아무나)',
    operation_description='일반 회원 로그인을 진행합니다',
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        properties={
            'email': openapi.Schema(type=openapi.TYPE_STRING, description="이메일"),
            'password': openapi.Schema(type=openapi.TYPE_STRING, description="비밀번호"),
        }
    ),
    tags=['유저'],
    responses={200: openapi.Response(
        description="200 OK",
        schema=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'access_token': openapi.Schema(type=openapi.TYPE_STRING, description="Access Token"),
                'refresh_token': openapi.Schema(type=openapi.TYPE_STRING, description="Refresh Token"),
            }
        )
    )}
)
@api_view(['POST'])
@permission_classes([AllowAny])
def login(request):
    email = request.data.get('email')
    password = request.data.get('password')

    user = authenticate(email=email, password=password)
    if user is None:
        return Response({'message': '이메일 인증이 완료되지 않았거나, 아이디 또는 비밀번호가 일치하지 않습니다.'}, status=status.HTTP_401_UNAUTHORIZED)

    refresh = RefreshToken.for_user(user)
    update_last_login(None, user)

    return Response({'access_token': str(refresh.access_token),
                    'refresh_token': str(refresh),}, status=status.HTTP_200_OK)

@swagger_auto_schema(
    method='post',
    operation_id='구글 로그인(아무나)',
    operation_description='구글 소셜 로그인을 진행합니다',
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        properties={
            'accessToken': openapi.Schema(type=openapi.TYPE_STRING, description="구글 인증 토큰"),
        }
    ),
    tags=['유저'],
    responses={200: openapi.Response(
        description="200 OK",
        schema=openapi.Schema(
            type=openapi.TYPE_OBJECT,
            properties={
                'access_token': openapi.Schema(type=openapi.TYPE_STRING, description="Access Token"),
                'refresh_token': openapi.Schema(type=openapi.TYPE_STRING, description="Refresh Token"),
            }
        )
    )}
)    
@api_view(['POST'])
@permission_classes([AllowAny])
def google_login(request):
    # 구글 토큰을 받아옴
    access_token = request.data.get('accessToken')
    userinfo_req = requests.get(
        f"https://www.googleapis.com/oauth2/v3/userinfo?access_token={access_token}")
    userinfo_req_status = userinfo_req.status_code
    if userinfo_req_status != 200:
        return JsonResponse({'message': '유저 정보를 얻어오는데 실패하였습니다.'}, status=status.HTTP_400_BAD_REQUEST)
    # 토큰에서 정보 추출
    userinfo = userinfo_req.json()
    email = userinfo['email']
    
    # 이미 가입된 유저인지 확인
    try:
        user = User.objects.get(email=email)
        social_user = SocialAccount.objects.get(user=user)
        # 기존에 일반회원으로 가입된 유저인지 확인
        if social_user is None:
            return JsonResponse({'message': '이미 일반회원으로 가입되어있는 유저입니다.'}, status=status.HTTP_400_BAD_REQUEST)
        data = {'access_token': access_token}
        accept = requests.post(
            f"{BASE_URL}api/user/login/google/finish/", data=data)
        accept_status = accept.status_code
        if accept_status != 200:
            return JsonResponse({'message': '로그인에 실패하였습니다.'}, status=accept_status)
        accept_json = accept.json()
        accept_json.pop('user', None)
        return JsonResponse(accept_json)
    except User.DoesNotExist:
        # 기존에 가입된 유저가 없으면 새로 가입
        data = {'access_token': access_token}
        accept = requests.post(
            f"{BASE_URL}api/user/login/google/finish/", data=data)
        accept_status = accept.status_code
        if accept_status != 200:
            return JsonResponse({'message': '로그인에 실패하였습니다.'}, status=accept_status)
        accept_json = accept.json()
        accept_json.pop('user', None)
        return JsonResponse(accept_json)
    
class GoogleLogin(SocialLoginView):
    adapter_class = google_view.GoogleOAuth2Adapter
    client_class = OAuth2Client

@swagger_auto_schema(
    method='post',
    operation_id='비밀번호 리셋',
    operation_description='임시 비밀번호를 생성해서 등록된 이메일로 발송',
    request_body=openapi.Schema(
        type=openapi.TYPE_OBJECT,
        properties={
            'email': openapi.Schema(type=openapi.TYPE_STRING, description="비밀번호를 초기화할 계정의 이메일 주소"),
            'name': openapi.Schema(type=openapi.TYPE_STRING, description="비밀번호를 초기화할 계정의 이름"),
        }
    ),
    tags=['유저'],
    responses={200: "", 404: "해당 이메일의 계정이 없음"}
)    
@api_view(['POST'])
@permission_classes([AllowAny])
def password_reset(request):
    email = request.data.get('email')
    name = request.data.get('name')

    user = get_object_or_404(User, email=email)
    if not user.name == name:
        return Response({"message": "이메일과 이름이 일치하지 않습니다."}, status=status.HTTP_404_NOT_FOUND)
   
    
    new_pw_len = 12 # 새 비밀번호 길이
    pw_candidate = string.ascii_letters + string.digits + string.punctuation 
    
    new_pw = ""
    for i in range(new_pw_len):
        new_pw += random.choice(pw_candidate)
    
    user.set_password(new_pw)
    user.save()
    
    message_data = """\
    <div style="font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 540px; height: 600px; border-top: 4px solid #212121; margin: 100px auto; padding: 30px 0; box-sizing: border-box;">
        <h1 style="margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;">
            <span style="font-size: 15px; margin: 0 0 10px 3px;">JRstock</span><br />
            <span style="color: #212121;">비밀번호 초기화</span> 안내입니다. </h1>
        <p style="font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;">
            안녕하세요.<br />
            초기화된 비밀번호는 다음과 같습니다.<br />
            <b style="color: #212121;">" %s "</b><br />
            감사합니다</p>
    </div>
    """ % (new_pw)
    
    mail_title = "JRstock 비밀번호 초기화 안내"
    mail_to = email
    send_email = EmailMessage(mail_title, message_data, to=[mail_to])
    send_email.content_subtype = "html"
    send_email.send()
    
    return Response(status=status.HTTP_200_OK)
    