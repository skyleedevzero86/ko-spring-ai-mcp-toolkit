import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "RAG & 네트워크 검색 강화 스트리밍 대화",
  description: "RAG와 네트워크 검색을 활용한 AI 채팅 애플리케이션",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body style={{ margin: 0, padding: 0, minHeight: '100vh' }}>{children}</body>
    </html>
  );
}
