'use client'

import { useState, useEffect, useRef } from 'react'

type ChatMode = 'DIRECT' | 'KNOWLEDGE_BASE' | 'INTERNET_SEARCH'

interface Message {
  id: string
  content: string
  isUser: boolean
}

export default function Home() {
  const [messages, setMessages] = useState<Message[]>([])
  const [input, setInput] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const [chatMode, setChatMode] = useState<ChatMode>('DIRECT')
  const [uploading, setUploading] = useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const eventSourceRef = useRef<EventSource | null>(null)
  const currentBotMessageRef = useRef<string>('')
  const userIdRef = useRef<string>(`user-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`)
  const toastRef = useRef<HTMLDivElement>(null)

  const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

  useEffect(() => {
    return () => {
      if (eventSourceRef.current) {
        eventSourceRef.current.close()
      }
    }
  }, [])

  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' })
    }
  }, [messages])

  const showToast = (message: string, type: 'success' | 'error' = 'success') => {
    if (toastRef.current) {
      toastRef.current.textContent = message
      toastRef.current.className = `show ${type}`
      setTimeout(() => {
        if (toastRef.current) {
          toastRef.current.className = toastRef.current.className.replace('show', '')
        }
      }, 3000)
    }
  }

  const connectSSE = () => {
    if (eventSourceRef.current) {
      eventSourceRef.current.close()
    }

    const userId = userIdRef.current
    const eventSource = new EventSource(`${API_BASE_URL}/sse/connect?userId=${userId}`)
    eventSourceRef.current = eventSource

    eventSource.addEventListener('add', (event) => {
      if (event.data && event.data.toLowerCase() !== 'null') {
        currentBotMessageRef.current += event.data
        updateBotMessage()
      }
    })

    eventSource.addEventListener('finish', () => {
      currentBotMessageRef.current = ''
      setIsLoading(false)
      eventSource.close()
      eventSourceRef.current = null
    })

    eventSource.onerror = () => {
      if (currentBotMessageRef.current) {
        updateBotMessage()
        currentBotMessageRef.current += '\n\n**연결이 끊어졌습니다. 다시 시도해주세요.**'
        updateBotMessage()
      }
      setIsLoading(false)
      eventSource.close()
      eventSourceRef.current = null
    }
  }

  const updateBotMessage = () => {
    setMessages((prev) => {
      const lastMessage = prev[prev.length - 1]
      if (lastMessage && !lastMessage.isUser) {
        return [
          ...prev.slice(0, -1),
          { ...lastMessage, content: currentBotMessageRef.current }
        ]
      } else {
        return [...prev, { id: Date.now().toString(), content: currentBotMessageRef.current, isUser: false }]
      }
    })
  }

  const sendMessage = async () => {
    const message = input.trim()
    if (!message || isLoading) return

    setMessages((prev) => [...prev, { id: Date.now().toString(), content: message, isUser: true }])
    setInput('')
    setIsLoading(true)
    currentBotMessageRef.current = ''
    setMessages((prev) => [...prev, { id: (Date.now() + 1).toString(), content: '', isUser: false }])

    connectSSE()

    try {
      const response = await fetch(`${API_BASE_URL}/chat/send`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({
          currentUserName: userIdRef.current,
          message: message,
          mode: chatMode,
        }),
      })

      if (!response.ok) {
        throw new Error('메시지 전송 실패')
      }
    } catch (error) {
      console.error('Failed to send message:', error)
      setMessages((prev) => {
        const lastMessage = prev[prev.length - 1]
        if (lastMessage && !lastMessage.isUser) {
          return [
            ...prev.slice(0, -1),
            { ...lastMessage, content: '**메시지 전송 실패, 백엔드 서비스를 확인하세요.**' }
          ]
        }
        return prev
      })
      setIsLoading(false)
      if (eventSourceRef.current) {
        eventSourceRef.current.close()
        eventSourceRef.current = null
      }
    }
  }

  const handleFileUpload = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file) return

    setUploading(true)
    const formData = new FormData()
    formData.append('file', file)

    try {
      const response = await fetch(`${API_BASE_URL}/rag/upload`, {
        method: 'POST',
        body: formData,
      })

      const result = await response.json()
      if (response.ok && result.status === 200) {
        showToast(`문서 "${file.name}" 업로드 성공!`, 'success')
        setChatMode('KNOWLEDGE_BASE')
      } else {
        showToast(`업로드 실패: ${result.msg || '알 수 없는 오류'}`, 'error')
      }
    } catch (error) {
      console.error('Upload failed:', error)
      showToast('업로드 실패, 네트워크를 확인하거나 관리자에게 문의하세요.', 'error')
    } finally {
      setUploading(false)
      event.target.value = ''
    }
  }

  const getPlaceholder = () => {
    switch (chatMode) {
      case 'KNOWLEDGE_BASE':
        return '업로드된 지식베이스를 기반으로 질문하세요...'
      case 'INTERNET_SEARCH':
        return '네트워크 검색을 통해 질문에 답변하겠습니다...'
      case 'DIRECT':
      default:
        return '직접 대화하세요...'
    }
  }

  return (
    <div className="chat-container">
      <div className="chat-header">RAG & 네트워크 검색 강화 스트리밍 대화</div>
      <div className="chat-messages" id="chat-messages">
        {messages.length === 0 ? (
          <div style={{ textAlign: 'center', color: '#999', marginTop: '2rem' }}>
            메시지를 입력하여 대화를 시작하세요
          </div>
        ) : (
          <>
            {messages.map((message) => (
              <div
                key={message.id}
                className={`message ${message.isUser ? 'user-message' : 'bot-message'}`}
                dangerouslySetInnerHTML={{ __html: message.content.replace(/\n/g, '<br>') }}
              />
            ))}
            <div ref={messagesEndRef} />
          </>
        )}
      </div>

      <div id="toast-notification" ref={toastRef}></div>

      <div className="chat-input-area">
        <label
          htmlFor="file-input"
          id="upload-button"
          className={uploading ? 'loading' : ''}
          title="지식 문서 업로드"
          style={{ pointerEvents: uploading ? 'none' : 'auto' }}
        >
          <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
            <path d="M761.6 364.8c-12.8-12.8-32-12.8-44.8 0l-160 160c-12.8 12.8-12.8 32 0 44.8 12.8 12.8 32 12.8 44.8 0l102.4-102.4v300.8c0 19.2 12.8 32 32 32s32-12.8 32-32V467.2l102.4 102.4c12.8 12.8 32 12.8 44.8 0 12.8-12.8 12.8-32 0-44.8L761.6 364.8zM896 896H128V128h448c19.2 0 32-12.8 32-32s-12.8-32-32-32H128C64 64 0 128 0 192v704c0 64 64 128 128 128h768c64 0 128-64 128-128V448c0-19.2-12.8-32-32-32s-32 12.8-32 32v448z"></path>
          </svg>
        </label>
        <input
          type="file"
          id="file-input"
          accept=".txt,.pdf,.md,.docx"
          onChange={handleFileUpload}
          disabled={uploading}
        />

        <div className="chat-mode-selector" title="대화 모드 선택">
          <select
            id="chat-mode-select"
            value={chatMode}
            onChange={(e) => setChatMode(e.target.value as ChatMode)}
          >
            <option value="DIRECT">직접 대화</option>
            <option value="KNOWLEDGE_BASE">지식베이스</option>
            <option value="INTERNET_SEARCH">네트워크 검색</option>
          </select>
        </div>

        <textarea
          id="message-input"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
              e.preventDefault()
              sendMessage()
            }
          }}
          placeholder={getPlaceholder()}
          rows={1}
          disabled={isLoading}
        />
        <button
          id="send-button"
          onClick={sendMessage}
          disabled={isLoading || !input.trim()}
        >
          전송
        </button>
      </div>
    </div>
  )
}
