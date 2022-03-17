import { apiInstance, loginApiInstance } from "./index.js";

const api = apiInstance();
const authApi = loginApiInstance();

// 주식 종목 리스트 받아오기
async function getStockItemList(page, size) {
  return await api.get(`stock/kospi/?page=${page}&size=${size}`);
}

// 종목 토론 리스트 받아오기
async function getBoardList(code, page, size) {
  return await api.get(
    `stock/post/${code}?` +
      (page && `page=${page}&`) +
      (size && `size=${size}&`)
  );
}

// 토론 게시글 작성
async function createBoard(title, content, code_number) {
  return await authApi.post("stock/post/create", {
    title,
    content,
    code_number,
  });
}

export { getStockItemList, getBoardList, createBoard };
